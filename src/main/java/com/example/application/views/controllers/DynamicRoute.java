package com.example.application.views.controllers;

import com.example.application.model.Category;
import com.example.application.model.Item;
import com.example.application.repository.CategoryRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.views.pages.categories.CategoriesViewCard;
import com.example.application.views.pages.items.ItemView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Route(value = ":path*", layout = StoreLayout.class)
@AnonymousAllowed
public class DynamicRoute extends VerticalLayout implements BeforeEnterObserver {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private OrderedList imageContainer;
    private OrderedList itemsContainer;
    private List<Item> itemss;
    private ItemView itemView;
    private List<Item> allItems;

    public DynamicRoute(CategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        allItems = new ArrayList<>();

        itemsContainer = new OrderedList();

        String path = event.getRouteParameters().get("path").orElse("");
        System.out.println("Path: " + path);

        Item items = itemRepository.findAll().stream()
                .filter(item -> item.getFullPath().equalsIgnoreCase(path))
                .findFirst()
                .orElse(null);

        if (items != null) {
            removeAll();
            showItemView(items);
            return;
        }

        // Sprawdź, czy ścieżka pasuje do kategorii
        String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        if(lastSegment.equals("koszyk")){
            UI.getCurrent().navigate("/koszyk/0");
        }

        Category category = categoryRepository.findBySlug(lastSegment)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category != null) {
            removeAll();

            constructUICategories(category.getName(), category.getDescription());

            List<Category> subcategories = categoryRepository.findByParentId(category.getId());
            subcategories.forEach(subcategory -> imageContainer.add(new CategoriesViewCard(subcategory)));

            List<Category> allCategories = getAllSubcategoriesIncludingCurrent(category);
            allCategories.addFirst(category);
            List<Category> parentCategories = getAllParentCategories(category);

            String categoryPathString = buildCategoryPath(parentCategories);
            System.out.println(categoryPathString);

            allItems.clear();

            allCategories.forEach(cat -> {
                itemss = itemRepository.findByCategoryId(cat.getId());
                allItems.addAll(itemss);
            });

            allItems.sort(Comparator.comparing(Item::getName, String::compareToIgnoreCase));
            itemView = new ItemView(allItems);

            add(itemView);

            return;
        }

        event.rerouteToError(NotFoundException.class);
    }

    private void showItemView(Item item) {
        removeAll();
        add(new H1("Produkt: " + item.getName()));
        add(new Div("Opis: " + item.getShortDescription()));
    }

    private void constructUICategories(String categoryHeader, String categoryDescription) {
        addClassNames("dprint-page-view");
        addClassNames(LumoUtility.MaxWidth.SCREEN_XLARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2(categoryHeader);
        header.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE, LumoUtility.FontSize.XXXLARGE);
        Paragraph description = new Paragraph(categoryDescription);
        description.addClassNames(LumoUtility.Margin.Bottom.XLARGE, LumoUtility.Margin.Top.NONE, LumoUtility.TextColor.SECONDARY);
        headerContainer.add(header, description);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.JustifyContent.CENTER);

        setWidthFull();
        add(headerContainer, imageContainer);

    }

    private List<Category> getAllSubcategoriesIncludingCurrent(Category category) {
        List<Category> allCategories = new ArrayList<>();
        //allCategories.add(category); // Dodaj bieżącą kategorię

        List<Category> directSubcategories = categoryRepository.findByParentId(category.getId());
        allCategories.addAll(directSubcategories);

        // Rekurencyjne pobieranie podkategorii
        for (Category subcategory : directSubcategories) {
            allCategories.addAll(getAllSubcategoriesIncludingCurrent(subcategory));
        }

        return allCategories;
    }

    private List<Category> getAllParentCategories(Category category) {
        List<Category> parentCategories = new ArrayList<>();
        parentCategories.add(category);

        // Rekurencyjnie pobierz kategorie nadrzędne
        while (category.getParent() != null) {
            category = categoryRepository.findById(category.getParent().getId()).orElse(null);
            if (category != null) {
                parentCategories.add(category);
            }
        }

        return parentCategories;
    }

    private String buildCategoryPath(List<Category> categories) {
        // Odwróć listę, ponieważ idziemy od najniższej kategorii do najwyższej
        Collections.reverse(categories);

        // Mapuj nazwy kategorii i połącz je za pomocą "/"
        return categories.stream()
                .map(Category::getSlug) // Pobierz nazwy kategorii
                .reduce((parent, child) -> parent + "/" + child)
                .orElse(""); // Zabezpieczenie na wypadek pustej listy
    }


}
