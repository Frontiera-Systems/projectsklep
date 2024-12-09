package com.example.application.views.pages;

import com.example.application.model.Item;
import com.example.application.repository.ItemRepository;
import com.example.application.views.controllers.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "product/:productId", layout = MainLayout.class)
@AnonymousAllowed
public class ProductDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final ItemRepository productRepository;
    private String productId;

    @Autowired
    public ProductDetailView(ItemRepository productRepository) {
        this.productRepository = productRepository;

        // Create initial content (will be updated in BeforeEnter)
        add(new Div(new H2("Loading...")));
    }

    // This method will be called before the view is rendered
   @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        RouteParameters parameters = beforeEnterEvent.getRouteParameters();
        productId = parameters.get("productId").orElse("");

        // Retrieve the product using the productId
        Item product = productRepository.findById((int) Long.parseLong(productId));


        // Clear previous content
        removeAll();

        // Display product details dynamically
        Div productDetail = new Div();
        productDetail.add(new H2(product.getName()));
        productDetail.add(new Div("ID: " + product.getId()));
        productDetail.add(new Div("Description: " + product.getShortDescription()));

        add(productDetail);
    }

}
