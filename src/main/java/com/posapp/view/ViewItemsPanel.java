package com.posapp.view;

import com.posapp.dao.ItemDAO;
import com.posapp.model.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

public class ViewItemsPanel extends JPanel {
    private ItemDAO itemDAO;

    private JTextField txtSearch;
    private JComboBox<String> cmbSearchCategory;
    private JButton btnSearch, btnAddNew, btnEdit, btnDelete;
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public ViewItemsPanel(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
        setLayout(new BorderLayout(10, 10));
        initUI();
        loadItemsTable(null, null);
    }

    private void initUI() {
        //  Top Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Items"));

        txtSearch = new JTextField(20);
        txtSearch.addActionListener(e -> searchItems());
        searchPanel.add(new JLabel("Name/Code:"));
        searchPanel.add(txtSearch);

        String[] categories = {"All", "Electronics", "Clothing", "Food", "Books", "Home Goods", "Other"};
        cmbSearchCategory = new JComboBox<>(categories);
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(cmbSearchCategory);

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchItems());
        searchPanel.add(btnSearch);


        add(searchPanel, BorderLayout.NORTH);

        //  Center Table Panel
        String[] columnNames = {"ID", "Name", "Code", "Category", "Retail Price", "Status", "Image Path"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Double.class;
                return super.getColumnClass(column);
            }
        };

        itemTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        itemTable.setRowSorter(sorter);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click to edit
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    editSelectedItem();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemTable);

        add(scrollPane, BorderLayout.CENTER);

        // Bottom Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAddNew = new JButton("Add New Item");
        btnAddNew.addActionListener(e -> addNewItem());
        btnEdit = new JButton("Edit Item");
        btnEdit.addActionListener(e -> editSelectedItem());
        btnDelete = new JButton("Delete Item");
        btnDelete.addActionListener(e -> deleteSelectedItem());

        buttonPanel.add(btnAddNew);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        // Add the buttonPanel to the SOUTH of the ViewItemsPanel
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void loadItemsTable(String searchTerm, String searchCategory) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);

            List<Item> items;
            if (searchTerm == null && searchCategory == null) {
                items = itemDAO.getAllItems();
            } else {
                items = itemDAO.searchItems(searchTerm, searchCategory);
            }

            for (Item item : items) {
                tableModel.addRow(new Object[]{
                        item.getId(),
                        item.getName(),
                        item.getCode(),
                        item.getCategory(),
                        item.getRetailPrice().doubleValue(),
                        item.getStatus(),
                        item.getImagePath()
                });
            }
        });
    }

    private void searchItems() {
        String searchTerm = txtSearch.getText().trim();
        String searchCategory = Objects.requireNonNull(cmbSearchCategory.getSelectedItem()).toString();
        loadItemsTable(searchTerm.isEmpty() ? null : searchTerm, searchCategory.equals("All") ? null : searchCategory);
    }

    private void addNewItem() {
        AddItemForm addItemForm = new AddItemForm(null, itemDAO);
        addItemForm.setVisible(true);
        if (addItemForm.isSavedSuccessfully()) {
            loadItemsTable(null, null);
        }
    }

    private void editSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }


        int modelRow = itemTable.convertRowIndexToModel(selectedRow);
        int itemId = (int) tableModel.getValueAt(modelRow, 0);

        Item itemToEdit = itemDAO.getItemById(itemId);
        if (itemToEdit != null) {
            AddItemForm editItemForm = new AddItemForm(null, itemDAO, itemToEdit);
            editItemForm.setVisible(true);
            if (editItemForm.isSavedSuccessfully()) {
                loadItemsTable(null, null);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Could not retrieve item details from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = itemTable.convertRowIndexToModel(selectedRow);
            int itemId = (int) tableModel.getValueAt(modelRow, 0);

            boolean success = itemDAO.deleteItem(itemId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadItemsTable(null, null); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}