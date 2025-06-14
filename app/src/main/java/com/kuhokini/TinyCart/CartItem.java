package com.kuhokini.TinyCart;

public class CartItem {
    private Object item;
    private int quantity;
    private double price;
    private double weight;
    private String name; // Added for better item identification

    public CartItem(Object item, String name, int quantity, double price, double weight) {
        this.item = item;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.weight = weight;
    }

    // Getters and setters
    public Object getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTotalPrice() {
        return price * quantity;
    }

    public double getTotalWeight() {
        return weight * quantity;
    }
}
