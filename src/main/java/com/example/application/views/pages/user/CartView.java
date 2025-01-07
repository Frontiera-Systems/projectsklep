package com.example.application.views.pages.user;

import com.example.application.model.Cart;
import com.example.application.model.CartItem;
import com.example.application.model.User;
import com.example.application.repository.CartItemRepository;
import com.example.application.repository.CartRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.repository.UserRepository;
import com.example.application.security.SecurityService;
import com.example.application.service.CartService;
import com.example.application.service.OrderService;
import com.example.application.service.SessionCartService;
import com.example.application.views.controllers.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "koszyk", layout = MainLayout.class)
@AnonymousAllowed
public class CartView extends HorizontalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final CartService cartService;
    private final SessionCartService sessionCartService;
    private final OrderService orderService;
    private Map<CartItem, Integer> temporaryQuantities = new HashMap<>();

    public CartView(ItemRepository itemRepository, CartItemRepository cartItemRepository, CartRepository cartRepository, UserRepository userRepository, SecurityService securityService, CartService cartService, SessionCartService sessionCartService, OrderService orderService) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.cartService = cartService;
        this.sessionCartService = sessionCartService;
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.orderService = orderService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Button order = orderButton();
        Long userId = securityService.getAuthenticatedUserId();
        if(userId.equals(0L)){
            constructSessionUI();
        } else {
            User user = userRepository.findById(userId);
            Cart cart = cartRepository.findByUserId(user.getId());
            constructUI(cart);
            }
        add(order);
    }


    private void constructUI(Cart cart) {
        removeAll();
        List<CartItem> itemsInCart = cartItemRepository.findByCartId(cart.getId());
        MultiSelectListBox<CartItem> itemList = cartService.multiitembox(itemsInCart, temporaryQuantities, cart);
        add(itemList);
    }

    private void constructSessionUI(){
        removeAll();
        Map<Integer, Integer> cart = sessionCartService.getCart();
        MultiSelectListBox<Integer> sessionItems = cartService.multiitemboxForSessionCart(sessionCartService,cart);
        add(sessionItems);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {

        System.out.println("beforeLeave triggered");
        temporaryQuantities.forEach((cartItem, quantity) -> {
            System.out.println("Updating: " + cartItem + " to quantity " + quantity);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        });
    }

    private Button orderButton(){
        Button orderButton = new Button("REALIZUJ ZAMOWIENIE");
        orderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SUCCESS);
        orderButton.addClickListener(click -> {
            UI.getCurrent().navigate("zamow");
        });
        return orderButton;
    }
}
