package com.posapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pos_db";
    private static final String USER = "root";
    private static final String PASSWORD = "ahinsa";



    private DBConnection() {

    }


    public static Connection getConnection() throws SQLException {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD);

            return newConnection;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found! Please check project dependencies. Details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);

            throw new SQLException("JDBC Driver not found", e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to database. Please check database server status and credentials. Details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }


}