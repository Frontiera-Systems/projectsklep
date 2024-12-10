package com.example.application.repository;

import com.example.application.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllById(Long id);
    List<Category> findAllByNameContainsIgnoreCase(String name);
    List<Category> findByParentId(Long parent_id);
    Optional<Category> findBySlug(String slug);
    List<Category> findByParentIsNull();

}
