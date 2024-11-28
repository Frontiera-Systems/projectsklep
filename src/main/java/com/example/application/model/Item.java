package com.example.application.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(of = "id")
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

    public double getPrice(){
        return price;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getName(){
        return name;
    }

}

