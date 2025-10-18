package com.posapp.view;

import com.posapp.dao.InvoiceDAO;
import com.posapp.model.Invoice;
import com.toedter.calendar.JDateChooser; // Requires JCalendar dependency

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


public class InvoiceHistoryPanel extends JPanel {

    private InvoiceDAO invoiceDAO;

    private JDateChooser dcStartDate, dcEndDate;
    private JTextField txtInvoiceNumberSearch;
    private JButton btnSearch, btnViewDetails, btnCancelInvoice;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public InvoiceHistoryPanel(InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadInvoiceTable(null, null, null); // Load all invoices initially
    }

    private void initUI() {
        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Invoices"));

        searchPanel.add(new JLabel("Invoice No:"));
        txtInvoiceNumberSearch = new JTextField(15);
        txtInvoiceNumberSearch.addActionListener(e -> searchInvoices());
        searchPanel.add(txtInvoiceNumberSearch);

        searchPanel.add(new JLabel("From:"));
        dcStartDate = new JDateChooser();
        dcStartDate.setPreferredSize(new Dimension(120, 25)); // Set preferred size for consistency
        searchPanel.add(dcStartDate);

        searchPanel.add(new JLabel("To:"));
        dcEndDate = new JDateChooser();
        dcEndDate.setPreferredSize(new Dimension(120, 25)); // Set preferred size
        searchPanel.add(dcEndDate);

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchInvoices());
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columnNames = {"ID", "Invoice No", "Date", "Billing Type", "Grand Total", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return BigDecimal.class;
                return super.getColumnClass(columnIndex);
            }
        };

        invoiceTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        invoiceTable.setRowSorter(sorter);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        invoiceTable.setDefaultRenderer(BigDecimal.class, new CurrencyTableCellRenderer());

        // Double-click to view details
        invoiceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    viewSelectedInvoiceDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnViewDetails = new JButton("View Details");
        btnViewDetails.addActionListener(e -> viewSelectedInvoiceDetails());
        btnCancelInvoice = new JButton("Cancel Invoice");
        btnCancelInvoice.addActionListener(e -> cancelSelectedInvoice());

        buttonPanel.add(btnViewDetails);
        buttonPanel.add(btnCancelInvoice);

        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void loadInvoiceTable(LocalDateTime startDate, LocalDateTime endDate, String invoiceNumber) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);

            List<Invoice> invoices;
            if (startDate == null && endDate == null && (invoiceNumber == null || invoiceNumber.trim().isEmpty())) {
                invoices = invoiceDAO.getAllInvoices();
            } else {
                invoices = invoiceDAO.searchInvoices(startDate, endDate, invoiceNumber);
            }

            for (Invoice invoice : invoices) {
                tableModel.addRow(new Object[]{
                        invoice.getId(),
                        invoice.getInvoiceNumber(),
                        invoice.getInvoiceDate().toLocalDate(),
                        invoice.getBillingType(),
                        invoice.getGrandTotal(),
                        invoice.getStatus()
                });
            }
        });
    }

    private void searchInvoices() {
        String invoiceNum = txtInvoiceNumberSearch.getText().trim();
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (dcStartDate.getDate() != null) {
            startDate = dcStartDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        if (dcEndDate.getDate() != null) {
            endDate = dcEndDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        loadInvoiceTable(startDate, endDate, invoiceNum.isEmpty() ? null : invoiceNum);
    }

    private void viewSelectedInvoiceDetails() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = invoiceTable.convertRowIndexToModel(selectedRow);
        int invoiceId = (int) tableModel.getValueAt(modelRow, 0); // Assuming ID is in the first column

        InvoiceDetailsDialog detailsDialog = new InvoiceDetailsDialog((JFrame) SwingUtilities.getWindowAncestor(this), invoiceDAO, invoiceId);
        detailsDialog.setVisible(true);
    }

    private void cancelSelectedInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = invoiceTable.convertRowIndexToModel(selectedRow);
        int invoiceId = (int) tableModel.getValueAt(modelRow, 0);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 5); // Assuming status is in column 5

        if ("Cancelled".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This invoice is already cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel invoice " + tableModel.getValueAt(modelRow, 1) + "? This action cannot be undone.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = invoiceDAO.updateInvoiceStatus(invoiceId, "Cancelled");
            if (success) {
                JOptionPane.showMessageDialog(this, "Invoice cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                searchInvoices(); // Refresh table to show updated status
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel invoice.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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