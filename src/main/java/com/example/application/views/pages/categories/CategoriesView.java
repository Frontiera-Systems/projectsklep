package com.example.application.views.pages.categories;

import com.example.application.model.Category;
import com.example.application.model.Item;
import com.example.application.repository.CategoryRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.views.controllers.StoreLayout;
import com.example.application.views.pages.DprintPageViewCard;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

@Route(value = ":categoryPath*", layout = StoreLayout.class)
//@PageTitle(value = ":categoryName")
@AnonymousAllowed
public class CategoriesView extends Main implements HasComponents, HasStyle, RouterLayout, BeforeEnterObserver {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private OrderedList imageContainer;
    private OrderedList itemsContainer;
    private String categoryId;

    public CategoriesView(CategoryRepository categoryRepository, ItemRepository itemRepository) {

        this.categoryRepository = categoryRepository;


        this.itemRepository = itemRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        RouteParameters parameters = beforeEnterEvent.getRouteParameters();
        //categoryId = parameters.get("categoryPath").orElse("");

        String categoryPath = parameters.get("categoryPath").orElse("");
        System.out.println("categoryPath: " + categoryPath);
        // Pobierz ostatni element z categoryPath jako ID kategorii
        String lastSegment = categoryPath.substring(categoryPath.lastIndexOf('/') + 1);
        Category category = categoryRepository.findBySlug(lastSegment)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Hibernate.initialize(category.getParent());
        Hibernate.initialize(category.getSubcategories());

        categoryId = String.valueOf(category.getId());

        removeAll();

        constructUICategories(category.getName(), category.getDescription());

        constructItemsUI();

        List<Category> subcategories = categoryRepository.findByParentId(category.getId());
        subcategories.forEach(subcategory -> imageContainer.add(new CategoriesViewCard(subcategory)));

        List<Category> allCategories = getAllSubcategoriesIncludingCurrent(category);

// Pobierz przedmioty dla wszystkich kategorii
        allCategories.forEach(cat -> {
            List<Item> items = itemRepository.findByCategoryId(cat.getId());
            items.forEach(product -> itemsContainer.add(new DprintPageViewCard(product)));
        });

       /* if (subcategories.isEmpty()) {
            List<Item> items = itemRepository.findByCategoryId(category.getId());
            items.forEach(product -> imageContainer.add(new DprintPageViewCard(product)));
        } else {
            subcategories.forEach(subcategory -> imageContainer.add(new CategoriesViewCard(subcategory)));
        }*/


        /*List<Item> items = itemRepository.findByCategoryId(category.getId());
        items.forEach(product -> itemsContainer.add(new DprintPageViewCard(product)));
*/

    }

    private void constructItemsUI() {
        addClassNames("dprint-page-view");
        addClassNames(LumoUtility.MaxWidth.SCREEN_XLARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sortuj");
        sortBy.setItems("Popularność", "Cena rosnąco", "Cena malejąco");
        sortBy.setValue("Popularność");


        itemsContainer = new OrderedList();
        itemsContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.JustifyContent.CENTER);

        container.add(sortBy);
        setWidthFull();
        add(container, itemsContainer);

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
        allCategories.add(category); // Dodaj bieżącą kategorię

        List<Category> directSubcategories = categoryRepository.findByParentId(category.getId());
        allCategories.addAll(directSubcategories);

        // Rekurencyjne pobieranie podkategorii
        for (Category subcategory : directSubcategories) {
            allCategories.addAll(getAllSubcategoriesIncludingCurrent(subcategory));
        }

        return allCategories;
    }


}
