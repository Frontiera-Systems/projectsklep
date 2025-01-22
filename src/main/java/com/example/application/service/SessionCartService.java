package com.example.application.service;

import com.example.application.model.Item;
import com.example.application.repository.ItemRepository;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SessionCartService {

    public final ItemRepository itemRepository;

    public SessionCartService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void addToCart(int itemId, int quantity) {
        Map<Integer, Integer> koszyk = getCart();
        Item item = itemRepository.findById(itemId);
        
        int totalquantity = koszyk.getOrDefault(itemId, 0) + quantity;

        if (totalquantity > item.getQuantity()){
            koszyk.put(itemId,item.getQuantity());
        } else {
            koszyk.put(itemId, totalquantity);
        }
        VaadinSession.getCurrent().setAttribute("koszyk", koszyk);
    }

    public void removeFromCart(int itemId) {
        Map<Integer, Integer> koszyk = getCart();
        koszyk.remove(itemId);
        VaadinSession.getCurrent().setAttribute("koszyk", koszyk);
    }

    public Map<Integer, Integer> getCart() {
        // Pobierz koszyk z sesji lub stwórz nowy
        Map<Integer, Integer> koszyk = (Map<Integer, Integer>) VaadinSession.getCurrent().getAttribute("koszyk");
        if (koszyk == null) {
            koszyk = new HashMap<>();
            VaadinSession.getCurrent().setAttribute("koszyk", koszyk);
        }
        return koszyk;
    }

    public double getTotalCartPrice() {
        Map<Integer, Integer> koszyk = getCart();

        return koszyk.entrySet().stream()
                .mapToDouble(entry -> {
                    int itemId = entry.getKey();
                    int quantity = entry.getValue();

                    // Pobierz przedmiot z repozytorium
                    Item item = itemRepository.findById(itemId);

                    // Jeśli przedmiot istnieje, przemnoż cenę przez ilość
                    if (item != null) {
                        return item.getPrice() * quantity;
                    }

                    // Jeśli przedmiotu nie ma, zwróć 0
                    return 0.0;
                })
                .sum(); // Suma wszystkich cen
    }



}
