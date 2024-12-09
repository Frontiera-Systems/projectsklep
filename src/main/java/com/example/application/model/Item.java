package com.example.application.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "items") // Określenie nazwy tabeli w bazie danych
public class Item {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generowanie ID
    private int id;

    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @Column(name = "quantity", nullable = false) // Określenie nazwy kolumny w tabeli
    private int quantity;

    @Getter
    @Column(name = "image_url")
    private String imageUrl;

    @Getter
    @Column(name = "short_description")
    private String shortDescription;

    @Getter
    @Column(name = "long_description")
    private String longDescription;

    @Getter
    @Column(name = "price", nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

}

