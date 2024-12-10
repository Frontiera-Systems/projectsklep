package com.example.application.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "category")
public class Category {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Getter
    @Column(name = "slug")
    private String slug;

    @Setter
    @Getter
    @Column(name = "image_url")
    private String imageUrl;

    @Setter
    @Getter
    @Column(name = "description", columnDefinition = "CLOB")
    @Lob
    private String description;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Getter
    @Setter
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subcategories;

    public String getFullPath(){
        if (parent != null){
            return parent.getFullPath() + "/" + slug;
        }
        return slug;
    }
}
