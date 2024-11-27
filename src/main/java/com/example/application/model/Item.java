package com.example.application.model;

import jakarta.persistence.*;

@Entity
@Table(name = "items") // Określenie nazwy tabeli w bazie danych
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generowanie ID
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity") // Określenie nazwy kolumny w tabeli
    private int quantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;

    @Column(name = "price")
    private double price;

    // Konstruktor, gettery i settery

    public Item() {
    }

    public Item(int quantity, String imageUrl, String shortDescription, String longDescription, String name, double price) {
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.name = name;
        this.price = price;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(){
        this.price = price;
    }


    // Gettery i settery dla pól
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}

