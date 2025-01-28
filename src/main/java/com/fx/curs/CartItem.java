package com.fx.curs;

public class CartItem {
    private String itemName;
    private String category;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public CartItem(String itemName, String category, int quantity, double unitPrice, double totalPrice) {
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
