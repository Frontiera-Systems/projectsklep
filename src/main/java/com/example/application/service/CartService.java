package com.example.application.service;

import com.example.application.model.Cart;
import com.example.application.model.CartItem;
import com.example.application.model.Item;
import com.example.application.model.User;
import com.example.application.repository.CartItemRepository;
import com.example.application.repository.CartRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    public boolean addItemToCart(Long userId, int productId, int quantity, int cartNumber) {

        // Znalezienie koszyka użytkownika
        List<Cart> cartList = cartRepository.findByUserId(userId);
        if (cartList.size() > cartNumber){
            Cart cart = cartList.get(cartNumber);
            Item item = itemRepository.findById(productId);
            // Dodanie przedmiotu do koszyka

            CartItem cartItem = new CartItem();
            cartItem.setItem(item);
            cartItem.setQuantity(quantity);

            cart.addItem(cartItem);
            cartItemRepository.save(cartItem);

            // Zapisz koszyk
            cartRepository.save(cart);
            return true;
        } else {
            System.out.println("Nie istnieje taki numer koszyka");
        }

        return false;
    }

    public List<CartItem> getItemsInCart(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    public Cart removeItemFromCart(Long userId, Long cartItemId, Long cartId) {
        Cart cart = cartRepository.findAllByIdAndUserId(cartId,userId);
        if (cart != null) {
            CartItem item = cartItemRepository.findById(cartItemId).orElse(null);
            if (item != null && item.getCart().equals(cart)) {
                cart.removeItem(item);
                cartItemRepository.delete(item);
                cartRepository.save(cart);
            }
        }
        return cart;
    }


}
