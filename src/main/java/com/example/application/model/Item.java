package com.example.application.model;

import com.github.slugify.Slugify;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

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
    @Column(name = "slug")
    private String slug;

    @Getter
    @Column(name = "long_description")
    private String longDescription;

    @Getter
    @Column(name = "price", nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public String getFullPath()
    {
        if (category == null) {
            return createSlug(name); // Jeśli brak kategorii, zwracamy tylko nazwę przedmiotu
        }

        // Pobierz wszystkie kategorie nadrzędne
        String parentCategories = category.getFullPath();
        return parentCategories + "/" + createSlug(name);
    }

    public String createSlug(String name){
        final Slugify slg = Slugify.builder().build();
        String result = slg.slugify(name);
        return result;
    }

}

