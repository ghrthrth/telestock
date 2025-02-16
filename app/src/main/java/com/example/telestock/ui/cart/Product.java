package com.example.telestock.ui.cart;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String imageUrl;

    public Product(int id, String name, double price, int quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }

    // **Добавляем сеттер для quantity**
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
