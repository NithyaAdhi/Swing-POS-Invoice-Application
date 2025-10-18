package com.posapp.dao;

import com.posapp.model.Invoice;
import com.posapp.model.InvoiceItem;
import com.posapp.util.DBConnection;

import javax.swing.JOptionPane;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class InvoiceDAO {


    public boolean addInvoice(Invoice invoice, List<InvoiceItem> invoiceItems) {
        String insertInvoiceSQL = "INSERT INTO invoices (invoice_number, invoice_date, billing_type, subtotal, discount, grand_total, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertInvoiceItemSQL = "INSERT INTO invoice_items (invoice_id, item_id, item_name_at_sale, item_code_at_sale, quantity, price_at_sale, total_line_item) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            //  Insert the main invoice
            try (PreparedStatement pstmtInvoice = conn.prepareStatement(insertInvoiceSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmtInvoice.setString(1, invoice.getInvoiceNumber());
                pstmtInvoice.setTimestamp(2, Timestamp.valueOf(invoice.getInvoiceDate()));
                pstmtInvoice.setString(3, invoice.getBillingType());
                pstmtInvoice.setBigDecimal(4, invoice.getSubtotal());
                pstmtInvoice.setBigDecimal(5, invoice.getDiscount());
                pstmtInvoice.setBigDecimal(6, invoice.getGrandTotal());
                pstmtInvoice.setString(7, invoice.getStatus());

                int affectedRows = pstmtInvoice.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating invoice failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmtInvoice.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        invoice.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating invoice failed, no ID obtained.");
                    }
                }
            }

            // Insert invoice items
            try (PreparedStatement pstmtInvoiceItem = conn.prepareStatement(insertInvoiceItemSQL)) {
                for (InvoiceItem item : invoiceItems) {
                    pstmtInvoiceItem.setInt(1, invoice.getId()); // Use the generated invoice ID
                    if (item.getItemId() != null) {
                        pstmtInvoiceItem.setInt(2, item.getItemId());
                    } else {
                        pstmtInvoiceItem.setNull(2, Types.INTEGER); // For nullable foreign key
                    }
                    pstmtInvoiceItem.setString(3, item.getItemNameAtSale());
                    pstmtInvoiceItem.setString(4, item.getItemCodeAtSale());
                    pstmtInvoiceItem.setInt(5, item.getQuantity());
                    pstmtInvoiceItem.setBigDecimal(6, item.getPriceAtSale());
                    pstmtInvoiceItem.setBigDecimal(7, item.getTotalLineItem());
                    pstmtInvoiceItem.addBatch();
                }
                pstmtInvoiceItem.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, "Transaction rolled back due to error.", "Database Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error rolling back transaction: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            JOptionPane.showMessageDialog(null, "Failed to save invoice: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public Invoice getInvoiceById(int id) {
        String sql = "SELECT id, invoice_number, invoice_date, billing_type, subtotal, discount, grand_total, status, created_at, updated_at FROM invoices WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); // Use try-with-resources here
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching invoice by ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }


    public List<InvoiceItem> getInvoiceItemsForInvoice(int invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT id, invoice_id, item_id, item_name_at_sale, item_code_at_sale, quantity, price_at_sale, total_line_item, created_at FROM invoice_items WHERE invoice_id = ?";
        try (Connection conn = DBConnection.getConnection(); // Use try-with-resources here
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToInvoiceItem(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching invoice items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return items;
    }


    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT id, invoice_number, invoice_date, billing_type, subtotal, discount, grand_total, status, created_at, updated_at FROM invoices ORDER BY invoice_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching all invoices: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return invoices;
    }


    public List<Invoice> searchInvoices(LocalDateTime startDate, LocalDateTime endDate, String invoiceNumber) {
        List<Invoice> invoices = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT id, invoice_number, invoice_date, billing_type, subtotal, discount, grand_total, status, created_at, updated_at FROM invoices WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (startDate != null) {
            sqlBuilder.append(" AND invoice_date >= ?");
            params.add(Timestamp.valueOf(startDate));
        }
        if (endDate != null) {
            sqlBuilder.append(" AND invoice_date <= ?");
            params.add(Timestamp.valueOf(endDate.withHour(23).withMinute(59).withSecond(59))); // End of day
        }
        if (invoiceNumber != null && !invoiceNumber.trim().isEmpty()) {
            sqlBuilder.append(" AND invoice_number LIKE ?");
            params.add("%" + invoiceNumber + "%");
        }
        sqlBuilder.append(" ORDER BY invoice_date DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching invoices: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return invoices;
    }


    public boolean updateInvoiceStatus(int invoiceId, String newStatus) {
        String sql = "UPDATE invoices SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating invoice status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to map ResultSet to Invoice object
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        return new Invoice(
                rs.getInt("id"),
                rs.getString("invoice_number"),
                rs.getTimestamp("invoice_date").toLocalDateTime(),
                rs.getString("billing_type"),
                rs.getBigDecimal("subtotal"),
                rs.getBigDecimal("discount"),
                rs.getBigDecimal("grand_total"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }

    // Helper method to map ResultSet to InvoiceItem object
    private InvoiceItem mapResultSetToInvoiceItem(ResultSet rs) throws SQLException {
        Integer itemId = rs.getObject("item_id") != null ? rs.getInt("item_id") : null;
        return new InvoiceItem(
                rs.getInt("id"),
                rs.getInt("invoice_id"),
                itemId,
                rs.getString("item_name_at_sale"),
                rs.getString("item_code_at_sale"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price_at_sale"),
                rs.getBigDecimal("total_line_item"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}