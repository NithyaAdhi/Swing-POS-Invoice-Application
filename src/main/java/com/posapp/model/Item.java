package com.posapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Item {
    private int id;
    private String name;
    private String code;
    private String imagePath;
    private BigDecimal cost;
    private BigDecimal wholesalePrice;
    private BigDecimal retailPrice;
    private BigDecimal labelPrice;
    private BigDecimal creditPrice;
    private String category;
    private String status; // Active, Inactive
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public Item(int id, String name, String code, String imagePath, BigDecimal cost, BigDecimal wholesalePrice,
                BigDecimal retailPrice, BigDecimal labelPrice, BigDecimal creditPrice, String category, String status,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.imagePath = imagePath;
        this.cost = cost;
        this.wholesalePrice = wholesalePrice;
        this.retailPrice = retailPrice;
        this.labelPrice = labelPrice;
        this.creditPrice = creditPrice;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for new item without ID and timestamps
    public Item(String name, String code, String imagePath, BigDecimal cost, BigDecimal wholesalePrice,
                BigDecimal retailPrice, BigDecimal labelPrice, BigDecimal creditPrice, String category, String status) {
        this.name = name;
        this.code = code;
        this.imagePath = imagePath;
        this.cost = cost;
        this.wholesalePrice = wholesalePrice;
        this.retailPrice = retailPrice;
        this.labelPrice = labelPrice;
        this.creditPrice = creditPrice;
        this.category = category;
        this.status = status;
    }

    public Item() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public BigDecimal getWholesalePrice() { return wholesalePrice; }
    public void setWholesalePrice(BigDecimal wholesalePrice) { this.wholesalePrice = wholesalePrice; }
    public BigDecimal getRetailPrice() { return retailPrice; }
    public void setRetailPrice(BigDecimal retailPrice) { this.retailPrice = retailPrice; }
    public BigDecimal getLabelPrice() { return labelPrice; }
    public void setLabelPrice(BigDecimal labelPrice) { this.labelPrice = labelPrice; }
    public BigDecimal getCreditPrice() { return creditPrice; }
    public void setCreditPrice(BigDecimal creditPrice) { this.creditPrice = creditPrice; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}