package com.example.application.views.pages;

import com.example.application.model.User;
import com.example.application.repository.UserRepository;
import com.example.application.security.SecurityService;
import com.example.application.service.CartService;
import com.example.application.views.controllers.MainLayout;
import com.example.application.views.controllers.StoreLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Route(value = "", layout = StoreLayout.class)
@PageTitle("STRONA GŁOWNA")
@AnonymousAllowed
@ParentLayout(MainLayout.class)
public class MainViewView extends HorizontalLayout implements RouterLayout {

    private final CartService cartService;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    public MainViewView(CartService cartService, AuthenticationContext authenticationContext, SecurityService securityService, UserRepository userRepository) {
        this.cartService = cartService;
        this.securityService = securityService;
        this.userRepository = userRepository;


        add(new Button("Nowy koszyk", e -> {
            User user = userRepository.findById(securityService.getAuthenticatedUserId());
           cartService.createCart(user);
        }));

        add(new Button("Dodaj do koszyka 0", e -> {
            cartService.addItemToCart(securityService.getAuthenticatedUserId(),5,10, 0);
        }));

        add(new Button("Dodaj do koszyka 1", e -> {
            cartService.addItemToCart(securityService.getAuthenticatedUserId(),5,10, 1);
        }));
    }



}
