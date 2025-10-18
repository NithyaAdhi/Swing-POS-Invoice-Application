package com.posapp.main;

import com.posapp.view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                System.err.println("Failed to set LookAndFeel: " + e);
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}