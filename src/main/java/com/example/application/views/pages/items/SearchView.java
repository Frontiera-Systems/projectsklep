package com.example.application.views.pages.items;

import com.example.application.model.Item;
import com.example.application.repository.ItemRepository;
import com.example.application.views.controllers.StoreLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Comparator;
import java.util.List;

@Route(value = "search/:path*", layout = StoreLayout.class)
@AnonymousAllowed
public class SearchView  extends Main implements HasComponents, HasStyle, RouterLayout, BeforeEnterObserver {


    private final ItemRepository itemRepository;
    private OrderedList itemsContainer;
    private List<Item> items;

    public SearchView(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

        String path = beforeEnterEvent.getRouteParameters().get("path").orElse("");
        String lastSegment = path.substring(path.lastIndexOf('/') + 1);

        items = itemRepository.findByNameContainingIgnoreCase(lastSegment);

        removeAll();

        itemsContainer = new OrderedList();
        items.sort(Comparator.comparing(Item::getName, String::compareToIgnoreCase));
        items.forEach(product -> itemsContainer.add(new ItemsViewCard(product)));

        ItemView itemView = new ItemView(items,itemsContainer);
        add(itemView);
    }

}
