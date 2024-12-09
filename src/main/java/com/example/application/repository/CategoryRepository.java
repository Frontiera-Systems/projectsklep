package com.example.application.repository;

import com.example.application.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllById(Long id);
    List<Category> findAllByNameContainsIgnoreCase(String name);
    List<Category> findByParentId(Long parent_id);
    Optional<Category> findBySlug(String slug);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent LEFT JOIN FETCH c.subcategories WHERE c.slug = :slug")
    Optional<Category> findBySlugWithParentAndSubcategories(@Param("slug") String slug);

}
