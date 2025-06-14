package com.kuhokini.TinyCart;
import java.util.HashMap;

public class TinyCart {
    private static TinyCart instance;
    private HashMap<Object, CartItem> items = new HashMap<>();
    private double totalPrice = 0.0;
    private double totalWeight = 0.0;
    private int totalQuantity = 0;

    // Private constructor to prevent instantiation
    private TinyCart() {}

    public static synchronized TinyCart getInstance() {
        if (instance == null) {
            instance = new TinyCart();
        }
        return instance;
    }

    // Add item with default quantity 1
    public void addItem(Object item, String name, double price, double weight) {
        addItem(item, name, 1, price, weight);
    }

    // Add item with specified quantity
    public void addItem(Object item, String name, int quantity, double price, double weight) {
        if (items.containsKey(item)) {
            CartItem cartItem = items.get(item);
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem(item, name, quantity, price, weight);
            items.put(item, cartItem);
        }
        updateTotals();
    }

    // Update item quantity, price and weight
    public void updateItem(Object item, int quantity, double price, double weight) {
        if (items.containsKey(item)) {
            CartItem cartItem = items.get(item);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(price);
            cartItem.setWeight(weight);
            updateTotals();
        }
    }

    public boolean itemAdded(Object item){
        if (items.containsKey(item)){
            return true;
        }else {
            return false;
        }
    }

    // Remove item from cart
    public void removeItem(Object item) {
        if (items.containsKey(item)) {
            items.remove(item);
            updateTotals();
        }
    }

    // Clear all items from cart
    public void clearCart() {
        items.clear();
        totalPrice = 0.0;
        totalWeight = 0.0;
        totalQuantity = 0;
    }

    // Recalculate all totals
    private void updateTotals() {
        totalPrice = 0.0;
        totalWeight = 0.0;
        totalQuantity = 0;

        for (CartItem cartItem : items.values()) {
            totalPrice += cartItem.getTotalPrice();
            totalWeight += cartItem.getTotalWeight();
            totalQuantity += cartItem.getQuantity();
        }
    }

    // Getters
    public double getTotalPrice() {
        return totalPrice;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public HashMap<Object, CartItem> getItems() {
        return items;
    }

    // Helper methods for formatted strings
    public String getFormattedTotalPrice() {
        return String.format("$%.2f", totalPrice);
    }

    public String getFormattedTotalWeight() {
        return String.format("%.2f kg", totalWeight);
    }

    public String getCartSummary() {
        return String.format("%d items, %s, Total: %s",
                totalQuantity, getFormattedTotalWeight(), getFormattedTotalPrice());
    }
}
