
# Java Swing POS Invoice Application


## 🚀 Project Overview

This project is a small desktop Point-Of-Sale (POS) Invoice Management System developed using Java Swing. It simulates core functionalities for managing product items, creating new invoices, and reviewing historical invoice data.

---

## ✨ Features Implemented

The application provides the following core functionalities:

1.  **Item Management:**
    *   Add new items with details like name, code (unique), various prices (cost, retail, wholesale, etc.), category, and status.
    *   Upload and store item images (copied to application directory for persistence).
    *   View all items in a sortable `JTable`.
    *   Search items by name, code, or category.
    *   Edit existing item details.
    *   Delete items.
    *   Input validation and success/error message dialogs for all operations.

2.  **Invoice Management (Create Invoice):**
    *   Search for items to add to the current invoice by name/code and category.
    *   Display selected item details and image preview.
    *   Add items to an invoice table, dynamically updating quantity if the item already exists.
    *   Adjust item quantities directly in the invoice table.
    *   Select billing type (Retail / Wholesale) to dynamically adjust item prices and totals in the invoice.
    *   Apply a discount to the invoice.
    *   Automatic calculation of subtotal, discount, and grand total.
    *   Save invoices and their line items transactionally to the database.
    *   Clear current invoice for a new transaction.

3.  **Invoice History:**
    *   View all past invoices in a sortable `JTable`.
    *   Search invoices by invoice number or a date range.
    *   View comprehensive details of a selected invoice, including its summary and a list of all sold items at the time of sale, in a dedicated dialog.
    *   Option to "Cancel" an existing invoice (updates status without deletion).

4.  **Database Persistence:**
    *   All item and invoice data (including invoice line items) is persistently stored in a MySQL database.
    *   Utilizes JDBC for database connectivity.

5.  **Robust Error Handling:**
    *   User-friendly error messages (e.g., for validation failures, database connection issues, duplicate item codes).
    *   Console logging for detailed exception tracking.

6.  **User Interface:**
    *   Clean and intuitive POS-style layout using Java Swing components.
    *   Tabbed interface for easy navigation between modules.
    *   Modern Look and Feel  applied for enhanced aesthetics.

---

## 🛠️ Technical Stack

*   **Language:** Java (JDK 18)
*   **UI Framework:** Java Swing
*   **Database:** MySQL 8.0+
*   **Data Access:** JDBC
*   **IDE:** IntelliJ IDEA
*   **Build Tool:** Apache Maven
*   **External Libraries:**
    *   `mysql-connector-j`: For MySQL database connectivity.
    *   `jcalendar`: Provides `JDateChooser` for user-friendly date input in Invoice History.
   

---

## ⚙️ Setup Instructions

Follow these steps to set up and run the application:

### 1. Prerequisites

*   **Java Development Kit (JDK):** Version 8 or later installed.
*   **MySQL Server:** Version 8.0 or later installed and running locally.
*   **MySQL Workbench (or similar client):** To manage your database.
*   **IntelliJ IDEA:** (Community Edition is sufficient)

### 2. Database Setup

1.  **Start MySQL Server:** Ensure your local MySQL server instance is running.
2.  **Execute SQL Script:**
    *   Open MySQL Workbench (or your preferred database client).
    *   Connect to your MySQL server (usually `localhost:3306`, `user: root`).
    *   Execute the `sql_script.sql` file provided in the project root. This script will:
        *   Create the database named `pos_db` if it doesn't exist.
        *   Use `pos_db`.
        *   Create the `items`, `invoices`, and `invoice_items` tables with the necessary schema and relationships.
3.  **Update Database Credentials:**
    *   Open `src/main/java/com/posapp/util/DBConnection.java`.
    *   **Change the `PASSWORD` variable** from `"your_mysql_password"` to your **actual MySQL `root` user password**.
    *   If your MySQL username or port is different, update `USER` and `URL` accordingly.

### 3. Project Setup (IntelliJ IDEA)

1.  **Open IntelliJ IDEA.**
2.  **Open Project:** Select `File` -> `Open...` and navigate to the root directory of the `pos-invoice-app` project (where `pom.xml` is located).
3.  **Load Maven Project:** IntelliJ should automatically detect it as a Maven project. If prompted, click "Load Maven Project" or "Enable Auto-Import" to download all necessary dependencies (like `mysql-connector-j`, `jcalendar`, `flatlaf`).
4.  **Verify Dependencies:** Ensure that the required libraries (listed under "Technical Stack" above) appear under "External Libraries" in your Project view.

### 4. Running the Application

1.  **Navigate** to `src/main/java/com/posapp/main/Main.java`.
2.  **Run:** Right-click on the `main` method or the `Main.java` file in the Project view and select `Run 'Main.main()'`.
3.  The main application window should appear with "Item Management" and "Create Invoice" (and "Invoice History") tabs.

---

## 📝 Usage Instructions

### 1. Item Management (Tab)
*   **Add New Item:** Click "Add New Item", fill in the form (including uploading an image), and click "Save".
*   **View Items:** All active items are displayed in the table upon loading the tab.
*   **Search Items:** Use the "Name/Code" text field or "Category" dropdown, then click "Search" to filter items.
*   **Edit Item:** Select an item in the table and click "Edit Item" (or double-click the row). Modify details in the form and "Save".
*   **Delete Item:** Select an item and click "Delete Item". Confirm the deletion.

### 2. Create Invoice (Tab)
*   **Search Items:** Use "Item Name/Code" and "Category" to find products to sell. Results appear in the left table.
*   **Add to Invoice:** Select an item from the search results, adjust "Quantity", and click "Add Item to Invoice". Items appear in the large central table. If an item is added again, its quantity updates.
*   **Adjust Quantity:** Directly double-click and edit the "Quantity" cell in the central invoice table. The line total and grand total will update.
*   **Billing Type:** Select "Retail" or "Wholesale" radio button. All item prices in the invoice table and the grand total will update dynamically.
*   **Discount:** Enter a discount amount in the "Discount" field. The "Grand Total" will update.
*   **Save Invoice:** Click "Save Invoice". A unique invoice number will be generated, and the invoice, along with its line items, will be saved to the database. The form will clear for a new invoice.
*   **Clear Invoice:** Clears the current invoice draft.

### 3. Invoice History (Tab)
*   **View Invoices:** All saved invoices are displayed upon loading the tab.
*   **Search Invoices:** Search by "Invoice No" (partial match) or specify a "From" and "To" date range using the calendar pickers, then click "Search".
*   **View Details:** Select an invoice and click "View Details" (or double-click the row) to open a dialog with a full summary and all line items of that specific invoice.
*   **Cancel Invoice:** Select an "Active" invoice and click "Cancel Invoice". Confirm to change its status to "Cancelled".

---


## 🔮 Future Enhancements 

*   Implement detailed inventory tracking (stock levels, alerts).
*   Customer management module.
*   User authentication with different roles/permissions.
*   Advanced reporting and analytics.
*   Integration with a barcode scanner.
*   More robust error logging and monitoring.
*   Network support for multi-user access.

---
