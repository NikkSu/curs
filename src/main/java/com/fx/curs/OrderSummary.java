package com.fx.curs;

public class OrderSummary {
    private final int orderId;
    private final String orderDate;
    private final int itemCount;
    private final double totalPrice;
    private final String status;

    public OrderSummary(int orderId, String orderDate, int itemCount, double totalPrice, String status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.itemCount = itemCount;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public int getItemCount() {
        return itemCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }
}
