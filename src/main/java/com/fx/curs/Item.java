package com.fx.curs;

public class Item {

    private int id;
    private String name;
    private String category;
    private int quantity;
    private int delivery;
    private double price;
    private String place;

    public Item(int id, String name, String category, int quantity, int delivery, double price, String place) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.delivery = delivery;
        this.price = price;
        this.place = place;
    }
    public Item(int id, String name, String category, int quantity, int delivery, double price ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.delivery = delivery;
        this.price = price;

    }

    public int getId() {
        return id;
    }
    public String getPlace(){
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDelivery() {
        return delivery;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
