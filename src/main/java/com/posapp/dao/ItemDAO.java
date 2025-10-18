package com.posapp.dao;

import com.posapp.model.Item;
import com.posapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ItemDAO {

    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (name, code, image_path, cost, wholesale_price, retail_price, label_price, credit_price, category, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getCode());
            pstmt.setString(3, item.getImagePath());
            pstmt.setBigDecimal(4, item.getCost());
            pstmt.setBigDecimal(5, item.getWholesalePrice());
            pstmt.setBigDecimal(6, item.getRetailPrice());
            pstmt.setBigDecimal(7, item.getLabelPrice());
            pstmt.setBigDecimal(8, item.getCreditPrice());
            pstmt.setString(9, item.getCategory());
            pstmt.setString(10, item.getStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setId(generatedKeys.getInt(1)); // Set generated ID back to item object
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null, "Item Code '" + item.getCode() + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error adding item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
        return false;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT id, name, code, image_path, cost, wholesale_price, retail_price, label_price, credit_price, category, status, created_at, updated_at FROM items";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return items;
    }

    public Item getItemById(int id) {
        String sql = "SELECT id, name, code, image_path, cost, wholesale_price, retail_price, label_price, credit_price, category, status, created_at, updated_at FROM items WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching item by ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateItem(Item item) {
        String sql = "UPDATE items SET name=?, code=?, image_path=?, cost=?, wholesale_price=?, retail_price=?, label_price=?, credit_price=?, category=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getCode());
            pstmt.setString(3, item.getImagePath());
            pstmt.setBigDecimal(4, item.getCost());
            pstmt.setBigDecimal(5, item.getWholesalePrice());
            pstmt.setBigDecimal(6, item.getRetailPrice());
            pstmt.setBigDecimal(7, item.getLabelPrice());
            pstmt.setBigDecimal(8, item.getCreditPrice());
            pstmt.setString(9, item.getCategory());
            pstmt.setString(10, item.getStatus());
            pstmt.setInt(11, item.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null, "Item Code '" + item.getCode() + "' already exists for another item.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error updating item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteItem(int id) {
        String sql = "DELETE FROM items WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    public List<Item> searchItems(String searchTerm, String searchCategory) {
        List<Item> items = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT id, name, code, image_path, cost, wholesale_price, retail_price, label_price, credit_price, category, status, created_at, updated_at FROM items WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(" AND (name LIKE ? OR code LIKE ?)");
            params.add("%" + searchTerm + "%");
            params.add("%" + searchTerm + "%");
        }
        if (searchCategory != null && !searchCategory.equals("All") && !searchCategory.trim().isEmpty()) {
            sqlBuilder.append(" AND category = ?");
            params.add(searchCategory);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return items;
    }

    // Helper method to map ResultSet to Item object
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("code"),
                rs.getString("image_path"),
                rs.getBigDecimal("cost"),
                rs.getBigDecimal("wholesale_price"),
                rs.getBigDecimal("retail_price"),
                rs.getBigDecimal("label_price"),
                rs.getBigDecimal("credit_price"),
                rs.getString("category"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}