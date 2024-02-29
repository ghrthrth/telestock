package com.example.massage_parlor.ui.gallery;

public class Card {
    private String photoUrl;
    private String title;
    private String description;
    private String price;

    public Card(String photoUrl, String username, String msg, String category, String categIncident) {
        this.photoUrl = photoUrl;
        this.title = username;
        this.description = msg;
        this.price = category;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }
}

