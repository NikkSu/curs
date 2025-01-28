package com.fx.curs;
public class Order {
    private final int orderId;
    private final int userId;
    private final String productName;
    private final String productLocation;
    private final int quantity;

    public Order(int orderId, int userId, String productName, String productLocation, int quantity) {
        this.orderId = orderId;
        this.userId = userId;
        this.productName = productName;
        this.productLocation = productLocation;
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductLocation() {
        return productLocation;
    }

    public int getQuantity() {
        return quantity;
    }
}
