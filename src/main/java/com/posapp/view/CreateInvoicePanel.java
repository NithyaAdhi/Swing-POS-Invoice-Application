package com.posapp.view;

import com.posapp.dao.InvoiceDAO;
import com.posapp.dao.ItemDAO;
import com.posapp.model.Invoice;
import com.posapp.model.InvoiceItem;
import com.posapp.model.Item;
import com.posapp.util.ImageUtil;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CreateInvoicePanel extends JPanel {

    private ItemDAO itemDAO;
    private InvoiceDAO invoiceDAO;

    //  Item Search & Selection Components
    private JTextField txtItemSearch;
    private JComboBox<String> cmbItemCategorySearch;
    private JButton btnSearchItem;
    private JTable searchResultsTable;
    private DefaultTableModel searchResultsTableModel;
    private JSpinner spinnerQuantity;
    private JButton btnAddItemToInvoice;
    private JLabel lblSelectedItemImage;
    private JLabel lblSelectedItemDetails;

    //  Invoice Items Table Components
    private JTable invoiceItemsTable;
    private DefaultTableModel invoiceItemsTableModel;

    // Totals & Actions Components
    private JRadioButton rbRetail, rbWholesale;
    private ButtonGroup billingTypeGroup;
    private JFormattedTextField txtDiscount;
    private JLabel lblSubtotalValue, lblGrandTotalValue;
    private JButton btnSaveInvoice, btnClearInvoice, btnCancelInvoice;

    private BigDecimal currentSubtotal = BigDecimal.ZERO;
    private BigDecimal currentDiscount = BigDecimal.ZERO;
    private BigDecimal currentGrandTotal = BigDecimal.ZERO;

    public CreateInvoicePanel(ItemDAO itemDAO, InvoiceDAO invoiceDAO) {
        this.itemDAO = itemDAO;
        this.invoiceDAO = invoiceDAO;
        setLayout(new BorderLayout(10, 10));
        initUI();
        setupInvoiceItemsTableListener();
        updateTotals();
        loadItemCategoriesForSearch();
    }

    private void initUI() {
        //  Item Search and Selection
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Add Items to Invoice"));

        // Search Controls
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtItemSearch = new JTextField(20);
        txtItemSearch.addActionListener(e -> searchAndDisplayItems());
        searchControls.add(new JLabel("Item Name/Code:"));
        searchControls.add(txtItemSearch);

        cmbItemCategorySearch = new JComboBox<>();
        searchControls.add(new JLabel("Category:"));
        searchControls.add(cmbItemCategorySearch);

        btnSearchItem = new JButton("Search Item");
        btnSearchItem.addActionListener(e -> searchAndDisplayItems());
        searchControls.add(btnSearchItem);
        topPanel.add(searchControls, BorderLayout.NORTH);

        // Search Results Table & Item Details
        JPanel searchResultAndDetailsPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 row, 2 columns

        // Search Results Table
        String[] searchColumnNames = {"ID", "Code", "Name", "Retail Price", "Wholesale Price"};
        searchResultsTableModel = new DefaultTableModel(searchColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        searchResultsTable = new JTable(searchResultsTableModel);
        searchResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displaySelectedItemDetails();
            }
        });
        searchResultAndDetailsPanel.add(new JScrollPane(searchResultsTable));

        // Selected Item Details & Add button
        JPanel itemAddPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        lblSelectedItemImage = new JLabel("No Image", SwingConstants.CENTER);
        lblSelectedItemImage.setPreferredSize(new Dimension(100, 100));
        lblSelectedItemImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        itemAddPanel.add(lblSelectedItemImage, gbc);

        gbc.gridy = 1;
        lblSelectedItemDetails = new JLabel("Select an item to add", SwingConstants.CENTER);
        itemAddPanel.add(lblSelectedItemDetails, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        itemAddPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1)); // Min 1, Max 999
        itemAddPanel.add(spinnerQuantity, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnAddItemToInvoice = new JButton("Add Item to Invoice");
        btnAddItemToInvoice.setFont(btnAddItemToInvoice.getFont().deriveFont(Font.BOLD, 14f)); // Make it larger
        btnAddItemToInvoice.addActionListener(e -> addItemToInvoice());
        itemAddPanel.add(btnAddItemToInvoice, gbc);

        searchResultAndDetailsPanel.add(itemAddPanel);
        topPanel.add(searchResultAndDetailsPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);


        // Invoice Items Table
        String[] invoiceColumnNames = {"Item ID", "Code", "Name", "Quantity", "Price", "Total", "OriginalItemID"}; // OriginalItemID hidden
        invoiceItemsTableModel = new DefaultTableModel(invoiceColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                if (columnIndex == 4 || columnIndex == 5) return BigDecimal.class;
                return super.getColumnClass(columnIndex);
            }
        };
        invoiceItemsTable = new JTable(invoiceItemsTableModel);

        invoiceItemsTable.getColumnModel().getColumn(6).setMinWidth(0);
        invoiceItemsTable.getColumnModel().getColumn(6).setMaxWidth(0);
        invoiceItemsTable.getColumnModel().getColumn(6).setWidth(0);


        invoiceItemsTable.setDefaultRenderer(BigDecimal.class, new CurrencyTableCellRenderer());

        JScrollPane invoiceScrollPane = new JScrollPane(invoiceItemsTable);
        add(invoiceScrollPane, BorderLayout.CENTER);


        // Totals, Billing Type, Actions
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Invoice Summary & Actions"));

        // Summary Panel
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // 3 rows, 2 columns for totals
        summaryPanel.add(new JLabel("Billing Type:"));
        JPanel billingTypeRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbRetail = new JRadioButton("Retail");
        rbWholesale = new JRadioButton("Wholesale");
        billingTypeGroup = new ButtonGroup();
        billingTypeGroup.add(rbRetail);
        billingTypeGroup.add(rbWholesale);
        rbRetail.setSelected(true);
        rbRetail.addActionListener(e -> updatePricesAndTotalsBasedOnBillingType());
        rbWholesale.addActionListener(e -> updatePricesAndTotalsBasedOnBillingType());
        billingTypeRadioPanel.add(rbRetail);
        billingTypeRadioPanel.add(rbWholesale);
        summaryPanel.add(billingTypeRadioPanel);

        summaryPanel.add(new JLabel("Subtotal:"));
        lblSubtotalValue = new JLabel("0.00");
        summaryPanel.add(lblSubtotalValue);

        // Discount field
        NumberFormat discountFormat = NumberFormat.getNumberInstance();
        discountFormat.setMinimumFractionDigits(2);
        discountFormat.setMaximumFractionDigits(2);
        NumberFormatter discountFormatter = new NumberFormatter(discountFormat);
        discountFormatter.setValueClass(BigDecimal.class);
        discountFormatter.setAllowsInvalid(false);
        discountFormatter.setOverwriteMode(true);
        txtDiscount = new JFormattedTextField(discountFormatter);
        txtDiscount.setValue(BigDecimal.ZERO);
        txtDiscount.setColumns(8);
        txtDiscount.addPropertyChangeListener("value", evt -> {
            currentDiscount = (BigDecimal) txtDiscount.getValue();
            if (currentDiscount == null || currentDiscount.compareTo(BigDecimal.ZERO) < 0) {
                currentDiscount = BigDecimal.ZERO;
                txtDiscount.setValue(BigDecimal.ZERO);
            }
            updateTotals();
        });
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        discountPanel.add(new JLabel("Discount:"));
        discountPanel.add(txtDiscount);
        summaryPanel.add(discountPanel);

        summaryPanel.add(new JLabel("Grand Total:"));
        lblGrandTotalValue = new JLabel("0.00");
        lblGrandTotalValue.setFont(lblGrandTotalValue.getFont().deriveFont(Font.BOLD, 16f)); // Make it stand out
        summaryPanel.add(lblGrandTotalValue);

        bottomPanel.add(summaryPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSaveInvoice = new JButton("Save Invoice");
        btnSaveInvoice.addActionListener(e -> saveInvoice());
        btnClearInvoice = new JButton("Clear Invoice");
        btnClearInvoice.addActionListener(e -> clearInvoice());


        actionButtonPanel.add(btnSaveInvoice);
        actionButtonPanel.add(btnClearInvoice);
        bottomPanel.add(actionButtonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadItemCategoriesForSearch() {

        cmbItemCategorySearch.addItem("All");

        String[] defaultCategories = {"Electronics", "Clothing", "Food", "Books", "Home Goods", "Other"};
        for (String category : defaultCategories) {
            cmbItemCategorySearch.addItem(category);
        }
    }

    private void searchAndDisplayItems() {
        String searchTerm = txtItemSearch.getText().trim();
        String searchCategory = Objects.requireNonNull(cmbItemCategorySearch.getSelectedItem()).toString();
        List<Item> items = itemDAO.searchItems(searchTerm.isEmpty() ? null : searchTerm, searchCategory.equals("All") ? null : searchCategory);

        searchResultsTableModel.setRowCount(0);
        for (Item item : items) {
            searchResultsTableModel.addRow(new Object[]{
                    item.getId(),
                    item.getCode(),
                    item.getName(),
                    item.getRetailPrice(),
                    item.getWholesalePrice()
            });
        }
        clearSelectedItemDetails();
    }

    private void displaySelectedItemDetails() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow != -1) {

            int modelRow = searchResultsTable.convertRowIndexToModel(selectedRow);
            int itemId = (int) searchResultsTableModel.getValueAt(modelRow, 0);
            Item selectedItem = itemDAO.getItemById(itemId);
            if (selectedItem != null) {
                lblSelectedItemDetails.setText(selectedItem.getName() + " (" + selectedItem.getCode() + ")");
                ImageIcon icon = ImageUtil.resizeImageIcon(selectedItem.getImagePath(), 100, 100);
                if (icon != null) {
                    lblSelectedItemImage.setIcon(icon);
                    lblSelectedItemImage.setText("");
                } else {
                    lblSelectedItemImage.setIcon(null);
                    lblSelectedItemImage.setText("No Image");
                }
            }
        }
    }

    private void clearSelectedItemDetails() {
        lblSelectedItemDetails.setText("Select an item to add");
        lblSelectedItemImage.setIcon(null);
        lblSelectedItemImage.setText("No Image");
    }

    private void addItemToInvoice() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the search results to add.", "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemId = (int) searchResultsTableModel.getValueAt(selectedRow, 0);
        Item itemToAdd = itemDAO.getItemById(itemId);

        if (itemToAdd == null) {
            JOptionPane.showMessageDialog(this, "Selected item not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity = (int) spinnerQuantity.getValue();
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be at least 1.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal price;
        if (rbRetail.isSelected()) {
            price = itemToAdd.getRetailPrice();
        } else {
            price = itemToAdd.getWholesalePrice();
        }
        BigDecimal totalLineItem = price.multiply(BigDecimal.valueOf(quantity));

        // Check if item already exists in the invoice table
        for (int i = 0; i < invoiceItemsTableModel.getRowCount(); i++) {

            if (Objects.equals(invoiceItemsTableModel.getValueAt(i, 0), itemToAdd.getId())) {
                int existingQty = (int) invoiceItemsTableModel.getValueAt(i, 3); // Quantity column
                invoiceItemsTableModel.setValueAt(existingQty + quantity, i, 3); // Update quantity
                // Price and total will be re-calculated by the TableModelListener
                JOptionPane.showMessageDialog(this, "Item quantity updated in invoice.", "Info", JOptionPane.INFORMATION_MESSAGE);
                spinnerQuantity.setValue(1); // Reset quantity
                return;
            }
        }

        // Add new item to invoice table
        invoiceItemsTableModel.addRow(new Object[]{
                itemToAdd.getId(), // Item ID for lookup (column 0)
                itemToAdd.getCode(),
                itemToAdd.getName(),
                quantity,
                price,
                totalLineItem,
                itemToAdd.getId()
        });

        updateTotals();
        spinnerQuantity.setValue(1);
    }

    private void setupInvoiceItemsTableListener() {
        invoiceItemsTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {

                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                    int row = e.getFirstRow();
                    if (row < 0) return;
                    try {
                        int newQuantity = (int) invoiceItemsTableModel.getValueAt(row, 3);
                        if (newQuantity <= 0) {
                            int option = JOptionPane.showConfirmDialog(CreateInvoicePanel.this,
                                    "Quantity must be greater than 0. Do you want to remove this item?",
                                    "Invalid Quantity", JOptionPane.YES_NO_OPTION);
                            if (option == JOptionPane.YES_OPTION) {

                                invoiceItemsTableModel.removeRow(row);
                                return;
                            } else {
                                invoiceItemsTableModel.setValueAt(1, row, 3); // Reset to 1

                            }
                        }

                        BigDecimal price = (BigDecimal) invoiceItemsTableModel.getValueAt(row, 4);
                        BigDecimal newTotal = price.multiply(BigDecimal.valueOf(newQuantity));
                        invoiceItemsTableModel.setValueAt(newTotal, row, 5); // Update total column

                    } catch (ClassCastException | NumberFormatException | NullPointerException ex) {
                        JOptionPane.showMessageDialog(CreateInvoicePanel.this,
                                "Please enter a valid number for quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        invoiceItemsTableModel.setValueAt(1, row, 3); // Reset to 1 on error
                        ex.printStackTrace();
                    }
                    updateTotals();
                }

                if (e.getType() == TableModelEvent.DELETE) {
                    updateTotals();
                }
            }
        });

        // Add a right-click popup menu for deleting items from the invoice
        invoiceItemsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = invoiceItemsTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < invoiceItemsTable.getRowCount()) {
                        invoiceItemsTable.setRowSelectionInterval(row, row);
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem deleteItem = new JMenuItem("Remove Item");
                        deleteItem.addActionListener(ev -> {
                            int modelRow = invoiceItemsTable.convertRowIndexToModel(row);
                            invoiceItemsTableModel.removeRow(modelRow);

                        });
                        popup.add(deleteItem);
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void updatePricesAndTotalsBasedOnBillingType() {

        for (int i = 0; i < invoiceItemsTableModel.getRowCount(); i++) {

            int originalItemId = (int) invoiceItemsTableModel.getValueAt(i, 6);

            Item item = itemDAO.getItemById(originalItemId);
            if (item != null) {
                BigDecimal newPrice;
                if (rbRetail.isSelected()) {
                    newPrice = item.getRetailPrice();
                } else {
                    newPrice = item.getWholesalePrice();
                }


                int quantity = (int) invoiceItemsTableModel.getValueAt(i, 3);
                BigDecimal newTotal = newPrice.multiply(BigDecimal.valueOf(quantity));

                invoiceItemsTableModel.setValueAt(newPrice, i, 4);
                invoiceItemsTableModel.setValueAt(newTotal, i, 5);
            }
        }
        updateTotals();
    }

    private void updateTotals() {
        BigDecimal calculatedSubtotal = BigDecimal.ZERO;
        for (int i = 0; i < invoiceItemsTableModel.getRowCount(); i++) {
            BigDecimal lineTotal = (BigDecimal) invoiceItemsTableModel.getValueAt(i, 5);
            calculatedSubtotal = calculatedSubtotal.add(lineTotal);
        }

        currentSubtotal = calculatedSubtotal;
        BigDecimal tempGrandTotal = currentSubtotal.subtract(currentDiscount);
        currentGrandTotal = tempGrandTotal.max(BigDecimal.ZERO);

        lblSubtotalValue.setText(String.format("%,.2f", currentSubtotal));
        lblGrandTotalValue.setText(String.format("%,.2f", currentGrandTotal));
    }

    private void saveInvoice() {
        if (invoiceItemsTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Invoice is empty. Please add items before saving.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentGrandTotal.compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(this, "Grand total cannot be negative. Adjust discount or items.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //  Create Invoice Object
        String invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime invoiceDate = LocalDateTime.now();
        String billingType = rbRetail.isSelected() ? "Retail" : "Wholesale";
        String status = "Active";

        Invoice newInvoice = new Invoice(invoiceNumber, invoiceDate, billingType,
                currentSubtotal, currentDiscount, currentGrandTotal, status);

        //  Create List of InvoiceItem Objects
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        for (int i = 0; i < invoiceItemsTableModel.getRowCount(); i++) {

            Integer originalItemId = (Integer) invoiceItemsTableModel.getValueAt(i, 6);

            String itemCodeAtSale = (String) invoiceItemsTableModel.getValueAt(i, 1);
            String itemNameAtSale = (String) invoiceItemsTableModel.getValueAt(i, 2);
            int quantity = (int) invoiceItemsTableModel.getValueAt(i, 3);
            BigDecimal priceAtSale = (BigDecimal) invoiceItemsTableModel.getValueAt(i, 4);
            BigDecimal totalLineItem = (BigDecimal) invoiceItemsTableModel.getValueAt(i, 5);

            InvoiceItem invItem = new InvoiceItem(originalItemId, itemNameAtSale, itemCodeAtSale,
                    quantity, priceAtSale, totalLineItem);
            invoiceItems.add(invItem);
        }

        // Call DAO to Save
        boolean success = invoiceDAO.addInvoice(newInvoice, invoiceItems);

        //  Show Result and Clear
        if (success) {
            JOptionPane.showMessageDialog(this, "Invoice " + newInvoice.getInvoiceNumber() + " saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearInvoice();
        } else {

            JOptionPane.showMessageDialog(this, "Failed to save invoice. Please check logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInvoice() {
        invoiceItemsTableModel.setRowCount(0);
        currentSubtotal = BigDecimal.ZERO;
        currentDiscount = BigDecimal.ZERO;
        currentGrandTotal = BigDecimal.ZERO;
        txtDiscount.setValue(BigDecimal.ZERO);
        rbRetail.setSelected(true);
        updateTotals();
        clearSelectedItemDetails();
        txtItemSearch.setText("");
        searchResultsTableModel.setRowCount(0);
        spinnerQuantity.setValue(1);
    }


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