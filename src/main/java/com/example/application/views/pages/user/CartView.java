package com.example.application.views.pages.user;

import com.example.application.model.Cart;
import com.example.application.model.CartItem;
import com.example.application.model.Item;
import com.example.application.model.User;
import com.example.application.repository.CartItemRepository;
import com.example.application.repository.CartRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.repository.UserRepository;
import com.example.application.security.SecurityService;
import com.example.application.service.CartService;
import com.example.application.service.SessionCartService;
import com.example.application.views.controllers.MainLayout;
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
    private Map<CartItem, Integer> temporaryQuantities = new HashMap<>();

    public CartView(ItemRepository itemRepository, CartItemRepository cartItemRepository, CartRepository cartRepository, UserRepository userRepository, SecurityService securityService, CartService cartService, SessionCartService sessionCartService) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.cartService = cartService;
        this.sessionCartService = sessionCartService;
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Long userId = securityService.getAuthenticatedUserId();
        if(userId.equals(0L)){
            constructSessionUI();
        } else {
            User user = userRepository.findById(userId);
            Cart cart = cartRepository.findByUserId(user.getId());
            constructUI(cart);
            }
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

    private void sessionCart(){
        removeAll();
        MultiSelectListBox<Integer> itemList = new MultiSelectListBox<>();
        Map<Integer, Integer> itemsInCart = sessionCartService.getCart();

        itemList.setItems(itemsInCart.keySet());

        Dialog confirmDelete = new Dialog();
        confirmDelete.setHeaderTitle("Na pewno chcesz usunac przedmiot z koszyka?");
        confirmDelete.setModal(false);
        Button confirmButton = new Button(new Icon(VaadinIcon.CHECK));
        Button declineButton = new Button(new Icon(VaadinIcon.CLOSE));
        confirmDelete.getFooter().add(confirmButton, declineButton);

        itemList.setRenderer(new ComponentRenderer<>(key -> {
            HorizontalLayout row = new HorizontalLayout();
            Integer quantity = itemsInCart.get(key);

            Item item = itemRepository.findById(Math.toIntExact(key));

            Image productImage = new Image(item.getImageUrl(), "");
            productImage.setWidth("50px");
            productImage.setHeight("50px");

            Span productName = new Span(item.getName());
            Span productIndeks = new Span("Indeks: " + item.getId());

            IntegerField cartitemquantity = new IntegerField();
            cartitemquantity.setStepButtonsVisible(true);
            cartitemquantity.setMin(0);
            cartitemquantity.setMax(item.getQuantity());
            cartitemquantity.setLabel("Ilość");
            cartitemquantity.setHelperText("Maksymalnie " + item.getQuantity() + " produktów");
            cartitemquantity.setValue(quantity);

            productIndeks.getStyle().set("color", "var(--lumo-secondary-text-color)").set("font-size", "var(--lumo-font-size-s)");

            VerticalLayout nameIndeks = new VerticalLayout(productName, productIndeks);
            row.add(productImage, nameIndeks, cartitemquantity);
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            return row;
        }));

        add(itemList);


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
}
