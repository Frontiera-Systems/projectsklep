package com.example.application.repository;

import com.example.application.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    CartItem findByItemId(Long itemId);
    Optional<CartItem> findByItemIdAndCartId(int itemId, Long cartId);
}
