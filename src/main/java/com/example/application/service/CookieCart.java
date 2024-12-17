package com.example.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import jakarta.servlet.http.Cookie;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CookieCart {

    private static final String CART_COOKIE_NAME = "cart_cookie";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void addToCart(Long itemId, int quantity) {
        Map<Long, Integer> cart = getCart();
        cart.put(itemId, cart.getOrDefault(itemId, 0) + quantity);
        saveCart(cart);
    }

    public static Map<Long, Integer> getCart() {
        VaadinRequest request = VaadinRequest.getCurrent();
        if (request == null) {
            throw new IllegalStateException("Nie można uzyskać bieżącego żądania!");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (CART_COOKIE_NAME.equals(cookie.getName())) {
                    try {
                        // Dekodowanie wartości Base64 z ciasteczka
                        String decodedValue = new String(Base64.getDecoder().decode(cookie.getValue()));
                        return objectMapper.readValue(decodedValue, Map.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return new HashMap<>(); // Nowy koszyk, jeśli brak ciasteczka
    }

    public static void removeFromCart(Long itemId) {
        Map<Long, Integer> cart = getCart();
        cart.remove(itemId);
        saveCart(cart);
    }

    private static void saveCart(Map<Long, Integer> cart) {
        VaadinResponse response = VaadinResponse.getCurrent();
        if (response == null) {
            throw new IllegalStateException("Nie można uzyskać bieżącej odpowiedzi!");
        }

        try {
            // Serializowanie danych do JSON
            String cartJson = objectMapper.writeValueAsString(cart);

            // Kodowanie JSON-a w Base64
            String encodedCart = Base64.getEncoder().encodeToString(cartJson.getBytes());

            Cookie cartCookie = new Cookie(CART_COOKIE_NAME, encodedCart);
            cartCookie.setPath("/");
            cartCookie.setMaxAge(60 * 60 * 24 * 7); // Ciasteczko ważne przez tydzień
            response.addCookie(cartCookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

