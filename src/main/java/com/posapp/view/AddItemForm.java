package com.posapp.view;

import com.posapp.dao.ItemDAO;
import com.posapp.model.Item;
import com.posapp.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Objects;

public class AddItemForm extends JDialog {
    private JTextField txtName, txtCode;
    private JFormattedTextField txtCost, txtWholesalePrice, txtRetailPrice, txtLabelPrice, txtCreditPrice;
    private JComboBox<String> cmbCategory, cmbStatus;
    private JLabel lblImagePreview;
    private JButton btnUploadImage, btnSave, btnCancel;

    private String currentImagePath;
    private ItemDAO itemDAO;
    private Item itemToEdit;

    private boolean savedSuccessfully = false;

    // Constructor for adding a new item
    public AddItemForm(JFrame parent, ItemDAO itemDAO) {
        this(parent, itemDAO, null);
    }

    // Constructor for editing an existing item
    public AddItemForm(JFrame parent, ItemDAO itemDAO, Item itemToEdit) {
        super(parent, itemToEdit == null ? "Add New Item" : "Edit Item", true); // Modal dialog
        this.itemDAO = itemDAO;
        this.itemToEdit = itemToEdit;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(parent);

        initUI();
        populateFieldsForEdit();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;


        addFormField(formPanel, gbc, "Item Name:", txtName = new JTextField(20), 0);
        addFormField(formPanel, gbc, "Item Code (Unique):", txtCode = new JTextField(20), 1);


        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        NumberFormatter currencyFormatter = new NumberFormatter(currencyFormat);
        currencyFormatter.setValueClass(BigDecimal.class);
        currencyFormatter.setAllowsInvalid(false);
        currencyFormatter.setOverwriteMode(true);

        txtCost = new JFormattedTextField(currencyFormatter);
        txtWholesalePrice = new JFormattedTextField(currencyFormatter);
        txtRetailPrice = new JFormattedTextField(currencyFormatter);
        txtLabelPrice = new JFormattedTextField(currencyFormatter);
        txtCreditPrice = new JFormattedTextField(currencyFormatter);


        txtCost.setValue(BigDecimal.ZERO);
        txtWholesalePrice.setValue(BigDecimal.ZERO);
        txtRetailPrice.setValue(BigDecimal.ZERO);
        txtLabelPrice.setValue(BigDecimal.ZERO);
        txtCreditPrice.setValue(BigDecimal.ZERO);

        addFormField(formPanel, gbc, "Cost:", txtCost, 2);
        addFormField(formPanel, gbc, "Wholesale Price:", txtWholesalePrice, 3);
        addFormField(formPanel, gbc, "Retail Price:", txtRetailPrice, 4);
        addFormField(formPanel, gbc, "Label Price:", txtLabelPrice, 5);
        addFormField(formPanel, gbc, "Credit Price:", txtCreditPrice, 6);

        // Categories
        String[] categories = {"Electronics", "Clothing", "Food", "Books", "Home Goods", "Other"};
        cmbCategory = new JComboBox<>(categories);
        addFormField(formPanel, gbc, "Category:", cmbCategory, 7);

        String[] statuses = {"Active", "Inactive"};
        cmbStatus = new JComboBox<>(statuses);
        addFormField(formPanel, gbc, "Status:", cmbStatus, 8);

        // Image Upload Section
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Item Image:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        btnUploadImage = new JButton("Upload Image");
        btnUploadImage.addActionListener(e -> uploadImage());
        formPanel.add(btnUploadImage, gbc);

        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.ipady = 100;
        lblImagePreview = new JLabel();
        lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagePreview.setVerticalAlignment(SwingConstants.CENTER);
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(lblImagePreview, gbc);
        gbc.ipady = 0; // Reset padding

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveItem());
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Helper method to add a label and component to the form with GridBagLayout
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST; // Changed to WEST for better alignment with shorter components
        panel.add(component, gbc);
    }

    private void populateFieldsForEdit() {
        if (itemToEdit != null) {
            txtName.setText(itemToEdit.getName());
            txtCode.setText(itemToEdit.getCode());
            txtCost.setValue(itemToEdit.getCost());
            txtWholesalePrice.setValue(itemToEdit.getWholesalePrice());
            txtRetailPrice.setValue(itemToEdit.getRetailPrice());
            txtLabelPrice.setValue(itemToEdit.getLabelPrice());
            txtCreditPrice.setValue(itemToEdit.getCreditPrice());
            cmbCategory.setSelectedItem(itemToEdit.getCategory());
            cmbStatus.setSelectedItem(itemToEdit.getStatus());

            currentImagePath = itemToEdit.getImagePath();
            if (currentImagePath != null && !currentImagePath.isEmpty()) {
                ImageIcon icon = ImageUtil.resizeImageIcon(currentImagePath, 150, 150);
                if (icon != null) {
                    lblImagePreview.setIcon(icon);
                    lblImagePreview.setText("");
                } else {
                    lblImagePreview.setIcon(null);
                    lblImagePreview.setText("Image not found");
                }
            } else {
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("No Image Selected");
            }
        } else {
            lblImagePreview.setText("No Image Selected");
        }
    }

    private void uploadImage() {
        File selectedFile = ImageUtil.chooseImageFile(this);
        if (selectedFile != null) {

            String copiedImagePath = ImageUtil.copyImageToAppDirectory(selectedFile);
            if (copiedImagePath != null) {
                currentImagePath = copiedImagePath;
                ImageIcon icon = ImageUtil.resizeImageIcon(currentImagePath, 150, 150);
                if (icon != null) {
                    lblImagePreview.setIcon(icon);
                    lblImagePreview.setText("");
                } else {
                    lblImagePreview.setIcon(null);
                    lblImagePreview.setText("Failed to load image (after copy)");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to copy image to application directory.", "Image Error", JOptionPane.ERROR_MESSAGE);
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("Failed to copy image");
                currentImagePath = null;
            }
        }
    }

    private void saveItem() {
        // Validation
        if (txtName.getText().trim().isEmpty() || txtCode.getText().trim().isEmpty() ||
                cmbCategory.getSelectedItem() == null || cmbStatus.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (Name, Code, Category, Status).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal cost, wholesalePrice, retailPrice, labelPrice, creditPrice;
        try {
            cost = (BigDecimal) txtCost.getValue();
            wholesalePrice = (BigDecimal) txtWholesalePrice.getValue();
            retailPrice = (BigDecimal) txtRetailPrice.getValue();
            labelPrice = (BigDecimal) txtLabelPrice.getValue();
            creditPrice = (BigDecimal) txtCreditPrice.getValue();

            // Ensure non-negative prices
            if (cost.compareTo(BigDecimal.ZERO) < 0 || wholesalePrice.compareTo(BigDecimal.ZERO) < 0 ||
                    retailPrice.compareTo(BigDecimal.ZERO) < 0 || labelPrice.compareTo(BigDecimal.ZERO) < 0 ||
                    creditPrice.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Prices cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (ClassCastException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for all price fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }


        //  Create/Update Item Object
        Item item;
        boolean isUpdate = (itemToEdit != null);

        if (isUpdate) {
            item = itemToEdit;
        } else {
            item = new Item();
        }

        item.setName(txtName.getText().trim());
        item.setCode(txtCode.getText().trim());
        item.setImagePath(currentImagePath);
        item.setCost(cost);
        item.setWholesalePrice(wholesalePrice);
        item.setRetailPrice(retailPrice);
        item.setLabelPrice(labelPrice);
        item.setCreditPrice(creditPrice);
        item.setCategory(Objects.requireNonNull(cmbCategory.getSelectedItem()).toString());
        item.setStatus(Objects.requireNonNull(cmbStatus.getSelectedItem()).toString());

        // Call DAO
        boolean success;
        if (isUpdate) {
            success = itemDAO.updateItem(item);
        } else {
            success = itemDAO.addItem(item);
        }

        // Show Result and Close
        if (success) {
            JOptionPane.showMessageDialog(this, "Item saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            savedSuccessfully = true;
            dispose();
        } else {

            JOptionPane.showMessageDialog(this, "Failed to save item. Check logs for details or if item code is duplicated.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSavedSuccessfully() {
        return savedSuccessfully;
    }
}