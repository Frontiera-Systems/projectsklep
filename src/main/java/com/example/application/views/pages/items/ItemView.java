package com.example.application.views.pages.items;

import com.example.application.model.Item;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ItemView extends Main implements HasComponents, HasStyle {

    public ItemView(List<Item> items, OrderedList itemsContainer) {
        addClassNames("dprint-page-view");
        addClassNames(LumoUtility.MaxWidth.SCREEN_XLARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        itemsContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.JustifyContent.CENTER);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sortuj");
        sortBy.setItems("Nazwa A-Z", "Nazwa Z-A","Cena rosnąco", "Cena malejąco");
        sortBy.setValue("Nazwa A-Z");

        OrderedList finalItemsContainer = itemsContainer;
        sortBy.addValueChangeListener(event -> {

            if(Objects.equals(event.getValue(), "Cena rosnąco")){
                items.sort(Comparator.comparing(Item::getPrice));
            }

            if(Objects.equals(event.getValue(), "Cena malejąco")){
                items.sort(Comparator.comparing(Item::getPrice).reversed());
            }

            if(Objects.equals(event.getValue(), "Nazwa A-Z")){
                items.sort(Comparator.comparing(Item::getName, String::compareToIgnoreCase));
            }

            if(Objects.equals(event.getValue(), "Nazwa Z-A")){
                items.sort(Comparator.comparing(Item::getName, String::compareToIgnoreCase).reversed());
            }
            finalItemsContainer.removeAll();
            items.forEach(product -> finalItemsContainer.add(new ItemsViewCard(product)));
        });

        itemsContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.JustifyContent.CENTER);

        container.add(sortBy);
        setWidthFull();
        add(container, itemsContainer);

    }



}
