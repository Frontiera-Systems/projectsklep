package com.example.application.views.pages;

import com.example.application.model.Item;
import com.example.application.repository.ItemRepository;
import com.example.application.views.controllers.MainLayout;
import com.example.application.views.controllers.StoreLayout;
import com.example.application.views.pages.items.ItemsViewCard;
import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@Route(value = "", layout = StoreLayout.class)
@PageTitle("STRONA GŁOWNA")
@AnonymousAllowed
@ParentLayout(MainLayout.class)

public class MainViewView extends VerticalLayout implements RouterLayout {

    private OrderedList itemsContainer;
    private List<Item> items;
    private final ItemRepository itemRepository;

    public MainViewView(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;

        itemsContainer = new OrderedList();
        itemsContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.JustifyContent.CENTER);

        setJustifyContentMode(JustifyContentMode.START);
        setAlignItems(Alignment.CENTER);
        Slide s1 = new Slide(createSlideContent("arduino-learning-kit","https://u.cubeupload.com/korylek/arduinokit.jpg"));
        Slide s2 = new Slide(createSlideContent("ender3-v3","https://u.cubeupload.com/korylek/ender3v3.jpg"));
        Slide s3 = new Slide(createSlideContent("flipper-zero","https://u.cubeupload.com/korylek/fiipperzero30.jpg"));

        Carousel c = new Carousel(s1,s2,s3).withAutoProgress()
                .withSlideDuration(4)
                .withStartPosition(1)
                .withoutSwipe();
        c.setWidth("550px");
        c.setHeight("300px");


        H3 akapit = new H3("CIEKAWE PRZEDMIOTY:");

        updateItems();

        add(c, akapit, itemsContainer);
    }

    public static Component createSlideContent(String url, String image) {
        Div result = new Div();
        result.addClassName("carousel-item");
        result.getStyle().set("background-image", "url('" + image + "')");
        result.addClickListener(event -> {
            UI.getCurrent().navigate(url);
        });
        return result;
    }

    private void updateItems() {
        itemsContainer.removeAll();
        items = itemRepository.findByNameContainingIgnoreCase("d");

        List<Item> pageItems = items.subList(0, 10);

        pageItems.forEach(item -> {
            ItemsViewCard card = new ItemsViewCard(item);
            card.addClassName("custom-item-style");  // Dodanie klasy CSS
            itemsContainer.add(card);
        });

    }

}

