package com.example.application.repository;

import com.example.application.model.Cart;
import com.example.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    Cart findAllByIdAndUserId(Long id, Long userId);
    Cart findByUser(User user);
}
