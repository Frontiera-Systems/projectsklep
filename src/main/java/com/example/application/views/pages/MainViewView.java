package com.example.application.views.pages;

import com.example.application.service.SessionCartService;
import com.example.application.views.controllers.MainLayout;
import com.example.application.views.controllers.StoreLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "", layout = StoreLayout.class)
@PageTitle("STRONA GŁOWNA")
@AnonymousAllowed
@ParentLayout(MainLayout.class)
public class MainViewView extends HorizontalLayout implements RouterLayout {

    private final SessionCartService sessionCartService;

    public MainViewView(SessionCartService sessionCartService) {
        this.sessionCartService = sessionCartService;


        add(new Button("Dodaj do koszyka", e -> {
            sessionCartService.addToCart(1, 2);
            sessionCartService.addToCart(4, 2);
            sessionCartService.addToCart(10, 2);
        }));


    }
}
