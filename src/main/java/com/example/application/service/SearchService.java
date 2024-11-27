package com.example.application.service;

import com.example.application.model.Item;
import com.example.application.repository.ItemRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    private final ItemRepository itemRepository;

    @Autowired
    public SearchService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public HorizontalLayout createSearchBar() {
        HorizontalLayout searchBar = new HorizontalLayout();
        searchBar.setWidth("30%");

        ComboBox<Item> searchBox = new ComboBox<>();
        Button searchButton = new Button(new Icon(VaadinIcon.SEARCH));
        searchBar.setSpacing(false);

        searchBox.setPlaceholder("Szukaj");
        searchBox.addClassName("no-arrow");
        searchBox.setWidth("300%");
        searchBox.getStyle().set("--vaadin-input-field-height", "50px");
        searchBox.setItemLabelGenerator(Item::getName);



        // Wstępne ustawienie wszystkich przedmiotów
        List<Item> allItems = itemRepository.findAll();
        searchBox.setItems(allItems);

        // Filtracja na podstawie wprowadzonego tekstu
        searchBox.addValueChangeListener(event -> {
            String filter = event.getValue() != null ? event.getValue().getName() : "";
            List<Item> filteredItems = itemRepository.findByNameContainingIgnoreCase(filter);
            searchBox.setItems(filteredItems); // Zaktualizowanie elementów w ComboBox
        });

        searchButton.getStyle().set("--vaadin-button-height", "50px");
        searchButton.setAriaLabel("Szukaj");

        // Dodanie listenera do przycisku
        searchButton.addClickListener(e -> {
            // Możesz tutaj dodać kod do działania przy kliknięciu przycisku
            // np. załadowanie wyników wyszukiwania
        });


        searchButton.getStyle().set("--vaadin-button-height", "50px");
        searchButton.setAriaLabel("Szukaj");

        searchBar.add(searchBox);
        searchBar.add(searchButton);
        searchBar.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER, LumoUtility.Padding.Vertical.XSMALL);
        return searchBar;
    }

}
