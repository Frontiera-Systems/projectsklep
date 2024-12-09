package com.example.application.repository;

import com.example.application.model.Item;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByNameContainingIgnoreCase(String name, PageRequest pageRequest);
    Item findById(int id);
    long countByNameContainingIgnoreCase(String name);
    List<Item> findByCategoryId(Long id);

}
