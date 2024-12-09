package com.example.application.views.pages;

import com.example.application.model.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

public class DprintPageViewCard extends ListItem {

    public DprintPageViewCard(Item item) {

        // Wywołaj pełny konstruktor z danymi produktu
        createView(item.getShortDescription(), item.getImageUrl(), item.getName(), item.getId(),
                item.getLongDescription(), item.getPrice());
    }

    public void createView(String shortdescription, String url, String name, int id, String longDescription, double price) {
        setWidth("20%");
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        div.setHeight("160px");

        Image image = new Image(url,shortdescription);
        image.setWidth("100%");
        div.add(image);

        Anchor nameLink = new Anchor("/id",name);


        Span header = new Span(nameLink);
        header.addClassNames(FontSize.MEDIUM, FontWeight.LIGHT);

        Div headerContainer = new Div(header);
        headerContainer.setHeight("30%");
        headerContainer.setWidth("100%");

        Span subtitle = new Span();
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        subtitle.setText("Indeks: " + id);

        Button cart = new Button(new Icon(VaadinIcon.CART));
        cart.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_ERROR);
        cart.setAriaLabel("Dodaj do koszyka");
        cart.setTooltipText("Dodaj do koszyka");

        Span badge = new Span();
        badge.setText(price + " zł");
        badge.addClassNames(FlexDirection.ROW, Display.FLEX, AlignItems.CENTER, "large-font");
        badge.setWidth("90%");

        HorizontalLayout priceBuy = new HorizontalLayout(badge, cart);

        add(div, headerContainer, subtitle, priceBuy);

    }
}
