package com.example.application.views.pages.items;

import com.example.application.model.Item;
import com.github.slugify.Slugify;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class ItemsViewCard extends ListItem {

    final Slugify slg = Slugify.builder().build();
    String result;

   public ItemsViewCard (Item item) {
       setWidth("20%");
       addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.START, LumoUtility.Padding.MEDIUM,
               LumoUtility.BorderRadius.LARGE);

       Div div = new Div();
       div.addClassNames(LumoUtility.Background.CONTRAST, LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER,
               LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Overflow.HIDDEN, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Width.FULL);
       div.setHeight("160px");

       Image image = new Image(item.getImageUrl(),item.getShortDescription());
       image.setWidth("100%");
       div.add(image);
        result = slg.slugify(item.getName());
       Anchor nameLink = new Anchor(item.getFullPath(),item.getName());


       Span header = new Span(nameLink);
       header.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.LIGHT);

       Div headerContainer = new Div(header);
       headerContainer.setHeight("30%");
       headerContainer.setWidth("100%");

       Span subtitle = new Span();
       subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
       subtitle.setText("Indeks: " + item.getId() + " Ilość: " + item.getQuantity());

       Button cart = new Button(new Icon(VaadinIcon.CART));
       cart.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_ERROR);
       cart.setAriaLabel("Dodaj do koszyka");
       cart.setTooltipText("Dodaj do koszyka");

       Span badge = new Span();
       badge.setText(item.getPrice() + " zł");
       badge.addClassNames(LumoUtility.FlexDirection.ROW, LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, "large-font");
       badge.setWidth("90%");

       HorizontalLayout priceBuy = new HorizontalLayout(badge, cart);

       add(div, headerContainer, subtitle, priceBuy);
   }
}
