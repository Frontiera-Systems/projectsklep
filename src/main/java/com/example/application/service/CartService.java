package com.example.application.service;

import com.example.application.model.Cart;
import com.example.application.model.CartItem;
import com.example.application.model.Item;
import com.example.application.repository.CartItemRepository;
import com.example.application.repository.CartRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ItemRepository itemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
    }

    public void addItemToCart(Long userId, int productId, int quantity) {

        // Znalezienie koszyka użytkownika
        Cart cart = cartRepository.findByUserId(userId);
        Item item = itemRepository.findById(productId);

        Optional<CartItem> cartItem = cartItemRepository.findByItemIdAndCartId(item.getId(), cart.getId());

        if (cartItem.isPresent()) {
            CartItem cartItem1 = cartItem.get();
            int totalquantity = cartItem1.getQuantity() + quantity;
            if (totalquantity > item.getQuantity()) {
                cartItem1.setQuantity(item.getQuantity());
            } else {
                cartItem1.setQuantity(totalquantity);
            }
            cartItemRepository.save(cartItem1);
        } else {
            CartItem cartItem2 = new CartItem();
            cartItem2.setItem(item);
            if (quantity > item.getQuantity()) {
                cartItem2.setQuantity(item.getQuantity());
            } else {
                cartItem2.setQuantity(quantity);
            }
            cart.addItem(cartItem2);
            cartItemRepository.save(cartItem2);
        }

        // Zapisz koszyk
        cartRepository.save(cart);
    }

    public List<CartItem> getItemsInCart(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);

    }

}
