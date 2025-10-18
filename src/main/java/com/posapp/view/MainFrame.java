package com.posapp.view;

import com.posapp.dao.InvoiceDAO;
import com.posapp.dao.ItemDAO;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private ItemDAO itemDAO;
    private InvoiceDAO invoiceDAO;

    public MainFrame() {
        super("POS Invoice Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);


        itemDAO = new ItemDAO();
        invoiceDAO = new InvoiceDAO();

        initUI();
    }

    private void initUI() {

        JTabbedPane tabbedPane = new JTabbedPane();

        // Item Management Panel
        ViewItemsPanel viewItemsPanel = new ViewItemsPanel(itemDAO);
        tabbedPane.addTab("Item Management", viewItemsPanel);

        // Create Invoice Panel
        CreateInvoicePanel createInvoicePanel = new CreateInvoicePanel(itemDAO, invoiceDAO);
        tabbedPane.addTab("Create Invoice", createInvoicePanel);

        //  Invoice History Panel
        InvoiceHistoryPanel invoiceHistoryPanel = new InvoiceHistoryPanel(invoiceDAO);
        tabbedPane.addTab("Invoice History", invoiceHistoryPanel);

        // Add the tabbed pane to the center of  MainFrame
        add(tabbedPane, BorderLayout.CENTER);
    }
}