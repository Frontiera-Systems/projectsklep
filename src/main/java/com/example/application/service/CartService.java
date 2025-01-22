package com.example.application.service;

import com.example.application.model.Cart;
import com.example.application.model.CartItem;
import com.example.application.model.Item;
import com.example.application.repository.CartItemRepository;
import com.example.application.repository.CartRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.repository.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    public double calculateTotalCartValue(Long userId) {
        // Znalezienie koszyka użytkownika
        Cart cart = cartRepository.findByUserId(userId);

        // Inicjalizacja sumy
        double totalValue = 0.0;

        // Iteracja przez wszystkie przedmioty w koszyku
        for (CartItem cartItem : cart.getItems()) {
            Item item = cartItem.getItem();  // Pobranie przedmiotu
            int quantity = cartItem.getQuantity();  // Ilość
            double itemPrice = item.getPrice();  // Cena jednostkowa

            // Dodaj cenę przedmiotu pomnożoną przez ilość do sumy
            totalValue += itemPrice * quantity;
        }

        return totalValue;
    }

    public List<CartItem> getItemsInCart(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);

    }

    public MultiSelectListBox<CartItem> multiitembox(
            List<CartItem> itemsInCart,
            Map<CartItem, Integer> temporaryQuantities,
            Cart cart
    ) {
        // Tworzenie instancji MultiSelectListBox
        MultiSelectListBox<CartItem> multiSelectListBox = new MultiSelectListBox<>();
        multiSelectListBox.setItems(itemsInCart);
        multiSelectListBox.select(itemsInCart);

        // Tworzenie dialogu potwierdzającego usunięcie
        Dialog confirmDelete = new Dialog();
        confirmDelete.setHeaderTitle("Na pewno chcesz usunąć przedmiot z koszyka?");
        confirmDelete.setModal(false);
        Button confirmButton = new Button(new Icon(VaadinIcon.CHECK));
        Button declineButton = new Button(new Icon(VaadinIcon.CLOSE));
        confirmDelete.getFooter().add(confirmButton, declineButton);

        // Konfiguracja renderera
        multiSelectListBox.setRenderer(new ComponentRenderer<>(cartItem -> {
            HorizontalLayout row = new HorizontalLayout();
            row.addClassName("cartitem-horizontal-layout");
            // Pobieranie danych o produkcie
            Item item = itemRepository.findById(Math.toIntExact(cartItem.getItem().getId()));
            multiSelectListBox.setItemEnabledProvider(status -> item.getQuantity() > 0);

            // Obrazek produktu
            Image productImage = new Image(item.getImageUrl(), "");
            productImage.setWidth("10%");
            productImage.setHeight("10%");

            // Dane produktu
            Span productName = new Span(item.getName());
            Span productIndex = new Span("Indeks: " + item.getId());
            productIndex.getStyle().set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");
            Span productPrice = new Span(item.getPrice()*cartItem.getQuantity() + " zł");

            // Pole ilości
            IntegerField cartItemQuantity = new IntegerField();
            cartItemQuantity.setMin(0);
            cartItemQuantity.setMax(item.getQuantity());
            cartItemQuantity.setLabel("Ilość");
            cartItemQuantity.setHelperText("/" + item.getQuantity());
            cartItemQuantity.setValue(cartItem.getQuantity());
            cartItemQuantity.setWidth("3%");
            // Obsługa zmiany ilości
            cartItemQuantity.addValueChangeListener(value -> {
                Integer newQuantity = value.getValue();
                if (newQuantity > 0) {
                    temporaryQuantities.put(cartItem, newQuantity);
                } else {
                    confirmDelete.open();
                    confirmButton.addClickListener(e -> {
                        removeCartItem(cartItem.getId());
                        List<CartItem> updatedItemsInCart = cartItemRepository.findByCartId(cart.getId());
                        multiSelectListBox.setItems(updatedItemsInCart);
                        confirmDelete.close();
                    });

                    declineButton.addClickListener(e -> {
                        confirmDelete.close();
                        cartItemQuantity.setValue(1);
                    });
                }
            });

            // Dodanie komponentów do układu
            VerticalLayout nameAndIndex = new VerticalLayout(productName, productIndex);
            row.add(productImage, nameAndIndex, cartItemQuantity);
            row.expand(nameAndIndex,productPrice,cartItemQuantity);
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            return row;
        }));

        return multiSelectListBox;
    }

    public MultiSelectListBox<Integer> multiitemboxForSessionCart(
            SessionCartService sessionCartService,
            Map<Integer, Integer> temporaryQuantities
    ) {
        // Pobierz aktualny koszyk z sesji
        Map<Integer, Integer> sessionCart = sessionCartService.getCart();

        // Tworzenie instancji MultiSelectListBox
        MultiSelectListBox<Integer> multiSelectListBox = new MultiSelectListBox<>();
        multiSelectListBox.addClassName("cartitem-multiselect");
        multiSelectListBox.setItems(sessionCart.keySet());
        multiSelectListBox.select(sessionCart.keySet());
        // Tworzenie dialogu potwierdzającego usunięcie
        Dialog confirmDelete = new Dialog();
        confirmDelete.setHeaderTitle("Na pewno chcesz usunąć przedmiot z koszyka?");
        confirmDelete.setModal(false);
        Button confirmButton = new Button(new Icon(VaadinIcon.CHECK));
        Button declineButton = new Button(new Icon(VaadinIcon.CLOSE));
        confirmDelete.getFooter().add(confirmButton, declineButton);

        // Konfiguracja renderera
        multiSelectListBox.setRenderer(new ComponentRenderer<>(itemId -> {
            HorizontalLayout row = new HorizontalLayout();
            row.addClassName("cartitem-horizontal-layout");
            // Pobieranie danych o produkcie
            Optional<Item> optionalItem = sessionCartService.itemRepository.findById(itemId);

            if (optionalItem.isEmpty()) {
                throw new RuntimeException("Produkt o ID " + itemId + " nie istnieje."); // Możesz tu zastosować własną obsługę błędu
            }

            Item item = optionalItem.get();

            // Obrazek produktu
            Image productImage = new Image(item.getImageUrl(), "");
            productImage.addClassName("cartitem-image");

            // Dane produktu
            Span productName = new Span(item.getName());
            Span productIndex = new Span("Indeks: " + item.getId());
            productIndex.getStyle().set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");
            Span productPrice = new Span(item.getPrice()*sessionCart.get(itemId) + " zł");

            // Pole ilości
            IntegerField cartItemQuantity = new IntegerField();;
            cartItemQuantity.setMin(0);
            cartItemQuantity.setMax(item.getQuantity());
            cartItemQuantity.setLabel("Ilość");
            cartItemQuantity.setHelperText("/" + item.getQuantity());
            cartItemQuantity.setValue(sessionCart.get(itemId));
            cartItemQuantity.addClassName("cartitem-quantity");

            // Obsługa zmiany ilości
            cartItemQuantity.addValueChangeListener(value -> {
                Integer newQuantity = value.getValue();
                if (newQuantity > 0) {
                    temporaryQuantities.put(itemId, newQuantity);
                    sessionCartService.addToCart(itemId, newQuantity - sessionCart.get(itemId));
                } else {
                    confirmDelete.open();
                    confirmButton.addClickListener(e -> {
                        sessionCartService.removeFromCart(itemId);
                        multiSelectListBox.setItems(sessionCartService.getCart().keySet());
                        confirmDelete.close();
                    });

                    declineButton.addClickListener(e -> {
                        confirmDelete.close();
                        cartItemQuantity.setValue(1);
                    });
                }
            });

            // Dodanie komponentów do układu
            VerticalLayout nameAndIndex = new VerticalLayout(productName, productIndex);
            nameAndIndex.setWidth("30%");
            row.add(productImage, nameAndIndex, productPrice, cartItemQuantity);
            //row.expand(nameAndIndex,productPrice,cartItemQuantity);
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            return row;
        }));

        return multiSelectListBox;
    }

}
