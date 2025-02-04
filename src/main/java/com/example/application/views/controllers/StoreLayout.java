package com.example.application.views.controllers;

import com.example.application.model.Category;
import com.example.application.model.Item;
import com.example.application.repository.CategoryRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.views.pages.items.ItemsViewCard;
import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

@AnonymousAllowed
@ParentLayout(MainCustomLayout.class)
public class StoreLayout extends HorizontalLayout implements RouterLayout {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private List<Item> items;
    private VerticalLayout leftMenus;

    public StoreLayout(CategoryRepository categoryRepository, ItemRepository itemRepository) {

        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        items = itemRepository.findTop5ByOrderByCreatedAtDesc();
        leftMenus = new VerticalLayout();
        leftMenus.addClassName("left-vertical-layout");
        addClassNames(LumoUtility.MaxWidth.SCREEN_XLARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);
        AccordionMenu();
        newItems();

        add(leftMenus);
    }

    public void AccordionMenu() {
        VerticalLayout accordionMenu = new VerticalLayout();
        accordionMenu.setClassName("left-cat-menu");

        // Pobierz wszystkie kategorie bez rodzica
        List<Category> rootCategories = categoryRepository.findByParentId(null);

        // Dla każdej kategorii na najwyższym poziomie twórz elementy menu
        rootCategories.forEach(rootCategory -> {
            VerticalLayout categoryContent = new VerticalLayout();
            categoryContent.setClassName("custom-details");
            createCategoryMenu(categoryContent, rootCategory);
            categoryContent.setPadding(false);
            Anchor anchor = new Anchor("/" + rootCategory.getFullPath(), rootCategory.getName().toUpperCase());
            anchor.addClassName("custom-text-bold");
            // Jeśli kategoria ma subkategorie, twórz rozwijane menu
            if (rootCategory.getSubcategories().isEmpty()) {
                // Jeśli nie ma subkategorii, dodaj zwykły link
                categoryContent.add(anchor);
                accordionMenu.add(categoryContent);
            } else {
                // Jeśli kategoria ma subkategorie, twórz rozwijane menu

                Details categoryDetails = new Details(anchor, categoryContent);
                categoryDetails.setOpened(rootCategory == rootCategories.getFirst());
                categoryDetails.addThemeVariants(DetailsVariant.REVERSE);
                categoryDetails.getElement().getStyle().set("width", "100%");// Ustalamy szerokość na 100%
                accordionMenu.add(categoryDetails);
            }
        });

        leftMenus.add(accordionMenu);
    }

    // Rekurencyjna metoda do tworzenia menu z subkategoriami
    private void createCategoryMenu(VerticalLayout parentLayout, Category category) {
        // Pobierz subkategorie tej kategorii
        List<Category> subcategories = categoryRepository.findByParentId(category.getId());

        // Jeśli są subkategorie, dodaj je do layoutu
        subcategories.forEach(subcategory -> {
            VerticalLayout subcategoryContent = new VerticalLayout();
            subcategoryContent.setPadding(false);
            createCategoryMenu(subcategoryContent, subcategory); // Rekurencyjnie dodaj subkategorie
            Anchor anchor = new Anchor("/" + subcategory.getFullPath(), subcategory.getName());
            anchor.addClassName("custom-text");
            // Jeśli subkategoria nie ma swoich subkategorii, nie twórz rozwijanego menu
            if (subcategory.getSubcategories().isEmpty()) {
                // Ostatnia subkategoria nie ma rozwijanego menu, ale powinna być widoczna
                subcategoryContent.add(anchor);
                parentLayout.add(subcategoryContent); // Dodaj subkategorię bez rozwijanego menu
            } else {
                // Tworzymy "Details" dla subkategorii, jeśli ma subkategorie
                Details subcategoryDetails = new Details(anchor, subcategoryContent);
                subcategoryDetails.setOpened(false);
                subcategoryDetails.addThemeVariants(DetailsVariant.SMALL, DetailsVariant.REVERSE);
                subcategoryDetails.getElement().getStyle().set("width", "100%");  // Ustalamy szerokość na 100%
                //subcategoryDetails.addClassName("custom-details");
                parentLayout.add(subcategoryDetails); // Dodaj subkategorię do głównego layoutu
            }
        });
    }

    private void newItems(){
        List<Slide> slides = new ArrayList<>();
        items.forEach(item -> {
            slides.add(new Slide(new ItemsViewCard(item)));
        });

        slides.forEach(slide -> {
            slide.addClassName("carousel-item-left");
        });

        Carousel newitemscarousel = new Carousel(slides.toArray(new Slide[0])).withAutoProgress().withSlideDuration(4)
                .withStartPosition(1)
                .withoutSwipe()
                .withoutNavigation();
        newitemscarousel.setWidthFull();
        newitemscarousel.setHeightFull();;
        leftMenus.add(newitemscarousel);
    }
}
