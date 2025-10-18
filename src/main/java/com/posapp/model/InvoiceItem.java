package com.posapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceItem {
    private int id;
    private int invoiceId;       // Foreign key to Invoice
    private Integer itemId;      // Foreign key to Item, can be null
    private String itemNameAtSale; // Snapshot of item name at time of sale
    private String itemCodeAtSale; // Snapshot of item code at time of sale
    private int quantity;
    private BigDecimal priceAtSale; // Snapshot of price at time of sale
    private BigDecimal totalLineItem; // quantity * priceAtSale
    private LocalDateTime createdAt;


    public InvoiceItem(int id, int invoiceId, Integer itemId, String itemNameAtSale, String itemCodeAtSale,
                       int quantity, BigDecimal priceAtSale, BigDecimal totalLineItem, LocalDateTime createdAt) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.itemId = itemId;
        this.itemNameAtSale = itemNameAtSale;
        this.itemCodeAtSale = itemCodeAtSale;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
        this.totalLineItem = totalLineItem;
        this.createdAt = createdAt;
    }

    // Constructor for creating a new invoice item
    public InvoiceItem(Integer itemId, String itemNameAtSale, String itemCodeAtSale,
                       int quantity, BigDecimal priceAtSale, BigDecimal totalLineItem) {
        this.itemId = itemId;
        this.itemNameAtSale = itemNameAtSale;
        this.itemCodeAtSale = itemCodeAtSale;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
        this.totalLineItem = totalLineItem;

    }


    public InvoiceItem() {}



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemNameAtSale() {
        return itemNameAtSale;
    }

    public void setItemNameAtSale(String itemNameAtSale) {
        this.itemNameAtSale = itemNameAtSale;
    }

    public String getItemCodeAtSale() {
        return itemCodeAtSale;
    }

    public void setItemCodeAtSale(String itemCodeAtSale) {
        this.itemCodeAtSale = itemCodeAtSale;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtSale() {
        return priceAtSale;
    }

    public void setPriceAtSale(BigDecimal priceAtSale) {
        this.priceAtSale = priceAtSale;
    }

    public BigDecimal getTotalLineItem() {
        return totalLineItem;
    }

    public void setTotalLineItem(BigDecimal totalLineItem) {
        this.totalLineItem = totalLineItem;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InvoiceItem{" +
                "id=" + id +
                ", invoiceId=" + invoiceId +
                ", itemId=" + itemId +
                ", itemNameAtSale='" + itemNameAtSale + '\'' +
                ", quantity=" + quantity +
                ", totalLineItem=" + totalLineItem +
                '}';
    }
}