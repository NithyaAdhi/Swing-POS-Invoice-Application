package com.posapp.view;

import com.posapp.dao.InvoiceDAO;
import com.posapp.model.Invoice;
import com.posapp.model.InvoiceItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceDetailsDialog extends JDialog {

    private InvoiceDAO invoiceDAO;
    private int invoiceId;

    // Invoice Header Components
    private JLabel lblInvoiceNumber, lblInvoiceDate, lblBillingType, lblSubtotal, lblDiscount, lblGrandTotal, lblStatus;

    // Invoice Items Table
    private JTable invoiceItemsTable;
    private DefaultTableModel invoiceItemsTableModel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();

    public InvoiceDetailsDialog(JFrame parent, InvoiceDAO invoiceDAO, int invoiceId) {
        super(parent, "Invoice Details", true);
        this.invoiceDAO = invoiceDAO;
        this.invoiceId = invoiceId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(parent);

        initUI();
        loadInvoiceDetails();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        //  Invoice Header Details
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Invoice Summary"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; headerPanel.add(new JLabel("Invoice No:"), gbc);
        gbc.gridx = 1; lblInvoiceNumber = new JLabel(); headerPanel.add(lblInvoiceNumber, gbc);
        gbc.gridx = 2; headerPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 3; lblInvoiceDate = new JLabel(); headerPanel.add(lblInvoiceDate, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; headerPanel.add(new JLabel("Billing Type:"), gbc);
        gbc.gridx = 1; lblBillingType = new JLabel(); headerPanel.add(lblBillingType, gbc);
        gbc.gridx = 2; headerPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3; lblStatus = new JLabel(); headerPanel.add(lblStatus, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; headerPanel.add(new JLabel("Subtotal:"), gbc);
        gbc.gridx = 1; lblSubtotal = new JLabel(); headerPanel.add(lblSubtotal, gbc);
        gbc.gridx = 2; headerPanel.add(new JLabel("Discount:"), gbc);
        gbc.gridx = 3; lblDiscount = new JLabel(); headerPanel.add(lblDiscount, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; headerPanel.add(new JLabel("Grand Total:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; lblGrandTotal = new JLabel();
        lblGrandTotal.setFont(lblGrandTotal.getFont().deriveFont(Font.BOLD, 16f));
        headerPanel.add(lblGrandTotal, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        //  Invoice Items Table
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Items in Invoice"));
        String[] columnNames = {"Item Code", "Item Name", "Quantity", "Price at Sale", "Total"};
        invoiceItemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class; // Quantity
                if (columnIndex == 3 || columnIndex == 4) return BigDecimal.class; // Price, Total
                return super.getColumnClass(columnIndex);
            }
        };
        invoiceItemsTable = new JTable(invoiceItemsTableModel);
        invoiceItemsTable.setDefaultRenderer(BigDecimal.class, new CurrencyTableCellRenderer());
        itemsPanel.add(new JScrollPane(invoiceItemsTable), BorderLayout.CENTER);
        mainPanel.add(itemsPanel, BorderLayout.CENTER);

        //  Close Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadInvoiceDetails() {
        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
        if (invoice != null) {
            lblInvoiceNumber.setText(invoice.getInvoiceNumber());
            lblInvoiceDate.setText(invoice.getInvoiceDate().format(DATE_FORMATTER));
            lblBillingType.setText(invoice.getBillingType());
            lblStatus.setText(invoice.getStatus());
            lblSubtotal.setText(CURRENCY_FORMATTER.format(invoice.getSubtotal()));
            lblDiscount.setText(CURRENCY_FORMATTER.format(invoice.getDiscount()));
            lblGrandTotal.setText(CURRENCY_FORMATTER.format(invoice.getGrandTotal()));

            List<InvoiceItem> items = invoiceDAO.getInvoiceItemsForInvoice(invoiceId);
            invoiceItemsTableModel.setRowCount(0);
            for (InvoiceItem item : items) {
                invoiceItemsTableModel.addRow(new Object[]{
                        item.getItemCodeAtSale(),
                        item.getItemNameAtSale(),
                        item.getQuantity(),
                        item.getPriceAtSale(),
                        item.getTotalLineItem()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invoice details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    // Reuse the CurrencyTableCellRenderer from CreateInvoicePanel
    private static class CurrencyTableCellRenderer extends DefaultTableCellRenderer {
        private final NumberFormat formatter = NumberFormat.getCurrencyInstance();

        public CurrencyTableCellRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof BigDecimal) {
                value = formatter.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}