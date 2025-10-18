package com.posapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice {
    private int id;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private String billingType; //  "Wholesale", "Retail"
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal grandTotal;
    private String status; //  "Active", "Cancelled"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    public Invoice(int id, String invoiceNumber, LocalDateTime invoiceDate, String billingType,
                   BigDecimal subtotal, BigDecimal discount, BigDecimal grandTotal, String status,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.billingType = billingType;
        this.subtotal = subtotal;
        this.discount = discount;
        this.grandTotal = grandTotal;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for creating a new invoice
    public Invoice(String invoiceNumber, LocalDateTime invoiceDate, String billingType,
                   BigDecimal subtotal, BigDecimal discount, BigDecimal grandTotal, String status) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.billingType = billingType;
        this.subtotal = subtotal;
        this.discount = discount;
        this.grandTotal = grandTotal;
        this.status = status;

    }


    public Invoice() {}



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", grandTotal=" + grandTotal +
                ", status='" + status + '\'' +
                '}';
    }
}