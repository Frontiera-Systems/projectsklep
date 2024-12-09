package com.example.application.views.controllers;

import com.example.application.model.Category;
import com.example.application.repository.CategoryRepository;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@AnonymousAllowed
@ParentLayout(MainLayout.class)
public class StoreLayout extends HorizontalLayout implements RouterLayout {

    private final CategoryRepository categoryRepository;

    public StoreLayout(CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
        addClassNames(LumoUtility.MaxWidth.SCREEN_XLARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidth("80%");  // Ustawiamy szerokość na 80%
        mainContent.setHeightFull();  // Ustawiamy pełną wysokość, by wypełniał dostępne miejsce
        AccordionMenu();

    }

/*    public void AccordionMenu(){
        VerticalLayout accoridonMenu = new VerticalLayout();
        accoridonMenu.setWidth("10%");
        accoridonMenu.setSpacing(true);

        VerticalLayout filamentsSubCoentent = new VerticalLayout(
                new Anchor("/product","PETG"),
                new Anchor("/p3","ABS"),
                new Anchor("/p3","TPU"),
                new Anchor("/p3","PLA+")
        );


        VerticalLayout minikomputeryContent = new VerticalLayout(
                new Details( new Anchor("/p3","Filamenty"),filamentsSubCoentent ),
                new Anchor("/p3","HotEnd"),
                new Anchor("/p3","Wydruki"),
                new Anchor("/p3","Bed"),
                new Anchor("/p3","Specyfiki")
        );

        Details printLeftMenu = new Details(new Anchor("druk3d","DRUK 3D"),minikomputeryContent);
        printLeftMenu.setOpened(true);
        printLeftMenu.addThemeVariants(DetailsVariant.FILLED);
        accoridonMenu.add(printLeftMenu);
        add(accoridonMenu);
    }*/

    public void AccordionMenu() {
        VerticalLayout accordionMenu = new VerticalLayout();
        accordionMenu.addClassNames(LumoUtility.Gap.XSMALL);
        accordionMenu.setWidth("10%");
        accordionMenu.setPadding(false);

        // Pobierz wszystkie kategorie bez rodzica
        List<Category> rootCategories = categoryRepository.findByParentId(null);

        // Dla każdej kategorii na najwyższym poziomie twórz elementy menu
        rootCategories.forEach(rootCategory -> {
            VerticalLayout categoryContent = new VerticalLayout();
            createCategoryMenu(categoryContent, rootCategory);
            categoryContent.setPadding(false);

            // Jeśli kategoria ma subkategorie, twórz rozwijane menu
            if (rootCategory.getSubcategories().isEmpty()) {
                // Jeśli nie ma subkategorii, dodaj zwykły link
                Anchor anchor = new Anchor("/" + rootCategory.getFullPath(), rootCategory.getName());
                categoryContent.add(anchor);
                accordionMenu.add(categoryContent);
            } else {
                // Jeśli kategoria ma subkategorie, twórz rozwijane menu
                Details categoryDetails = new Details(new Anchor("/" + rootCategory.getFullPath(), rootCategory.getName()), categoryContent);
                categoryDetails.setOpened(false);
                categoryDetails.addThemeVariants(DetailsVariant.FILLED, DetailsVariant.REVERSE);
                categoryDetails.getElement().getStyle().set("width", "100%");  // Ustalamy szerokość na 100%
                accordionMenu.add(categoryDetails);
            }
        });

        add(accordionMenu);
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

            // Jeśli subkategoria nie ma swoich subkategorii, nie twórz rozwijanego menu
            if (subcategory.getSubcategories().isEmpty()) {
                // Ostatnia subkategoria nie ma rozwijanego menu, ale powinna być widoczna
                Anchor anchor = new Anchor("/" + subcategory.getFullPath(), subcategory.getName());
                subcategoryContent.add(anchor);
                parentLayout.add(subcategoryContent); // Dodaj subkategorię bez rozwijanego menu
            } else {
                // Tworzymy "Details" dla subkategorii, jeśli ma subkategorie
                Details subcategoryDetails = new Details(new Anchor("/" + subcategory.getFullPath(), subcategory.getName()), subcategoryContent);
                subcategoryDetails.setOpened(false);
                subcategoryDetails.addThemeVariants(DetailsVariant.SMALL, DetailsVariant.REVERSE);
                subcategoryDetails.getElement().getStyle().set("width", "100%");  // Ustalamy szerokość na 100%
                parentLayout.add(subcategoryDetails); // Dodaj subkategorię do głównego layoutu
            }
        });
    }
}
