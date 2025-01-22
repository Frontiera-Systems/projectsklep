package com.example.application.views.pages.products;

import com.example.application.model.Item;
import com.example.application.repository.ItemRepository;
import com.example.application.views.controllers.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@Route(value = "product", layout = MainLayout.class)
@PageTitle("STRONA GŁOWNA")
@AnonymousAllowed

public class ProductView extends VerticalLayout {
    private final ItemRepository itemRepository;

    public ProductView(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;

        List<Item> items = itemRepository.findAll();

        // Create a Grid for displaying products
        Grid<Item> itemGrid = new Grid<>(Item.class);

        // Set up columns
        itemGrid.removeAllColumns();

        itemGrid.addColumn(new ComponentRenderer<>(product -> createView(product)))
                .setHeader("Product Details");

        // Set items for the grid
        itemGrid.setItems(items);

        // Add grid to the layout
        add(itemGrid);
    }

    private HorizontalLayout createView(Item product) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");

        // Image for the product
        Image image = new Image(product.getImageUrl(), product.getShortDescription());
        image.setWidth("160px");
        layout.add(image);

        // Product name with link
        Anchor nameLink = new Anchor("product/" + product.getId(), product.getName());
        Span header = new Span(nameLink);
        header.getStyle().set("font-size", "20px").set("font-weight", "600");

        // Short description
        Span subtitle = new Span("Indeks: " + product.getId());
        subtitle.getStyle().set("font-size", "12px").set("color", "gray");

        // Price
        Span price = new Span(product.getPrice() + " zł");
        price.getStyle().set("font-size", "16px").set("font-weight", "bold");

        // Add the components to the layout
        VerticalLayout details = new VerticalLayout(header, subtitle, price);
        details.setSpacing(false);
        details.setPadding(false);

        // Button to add to cart
        Button cart = new Button(new Icon(VaadinIcon.CART));
        cart.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY);
        cart.setAriaLabel("Add to cart");

        // Create a horizontal layout for price and button
        HorizontalLayout priceBuy = new HorizontalLayout(price, cart);
        priceBuy.setSpacing(true);

        // Add everything to the main layout
        layout.add(details, priceBuy);

        return layout;
    }
}
