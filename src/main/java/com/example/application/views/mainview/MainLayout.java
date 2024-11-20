package com.example.application.views.mainview;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private Nav breadcrumbNav;
    private OrderedList breadcrumbList;

    public MainLayout() {
        addNavbarContent(); // Dodajemy tylko navbar z breadcrumb
    }


    private void addNavbarContent() {

        // MENU BARS do wsadzenia do jakies klasy
        // MENU BARS ANCHORS


        Icon logo = new Icon();
        logo.setIcon(VaadinIcon.CLOUD_DOWNLOAD);
        logo.setSize("100px");

        // Breadcrumb navigation
        HorizontalLayout navbar = new HorizontalLayout();
        VerticalLayout menuBar = new VerticalLayout();

        menuBar.add(navbar);
        //navbar.setPadding(true);
        //navbar.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Width.MEDIUM); // Ustawienia stylu dla navbaru


        // Tworzymy nagłówek tylko z breadcrumb i tytułem widoku
        /*var header = new Header(breadcrumbNav);
        header.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                LumoUtility.Padding.End.MEDIUM, LumoUtility.Width.FULL);
        header.getStyle().set("flex-grow", "1");*/
        // Dodanie breadcrumb do navbaru
        menuBar.add(menuBars());
        menuBar.add(breadcrumb());
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        navbar.add(logo);
        navbar.add(searchBar());
        //navbar.add(menuBars());
        navbar.setWidthFull(); // Ustaw navbar na pełną szerokość
        navbar.setSpacing(true); // Dodaj odstępy między elementami

        addToNavbar(menuBar);
    }


    private void updateBreadcrumb(String path) {
        breadcrumbList.removeAll(); // Czyszczenie breadcrumb

        String[] segments = path.split("/");

        // Dodanie Home jako pierwszego elementu
        ListItem homeItem = new ListItem();
        homeItem.addClassNames(LumoUtility.Display.FLEX);
        Anchor home = new Anchor("/", "Strona Główna");
        home.getStyle().set("color", "rgba(0, 0, 0, 0.6)");
        homeItem.add(home);
        breadcrumbList.add(homeItem);

        // Generowanie pozostałych elementów breadcrumb
        StringBuilder currentPath = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (!segment.isEmpty()) {
                currentPath.append("/").append(segment);

                ListItem listItem = new ListItem();
                listItem.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);

                // Dodanie strzałki
                Icon icon = new Icon("lumo:angle-right");
                icon.addClassNames(LumoUtility.IconSize.SMALL, "mx-xs");
                listItem.add(icon);

                // Dodanie Anchor z nazwą segmentu
                Anchor anchor = new Anchor(currentPath.toString(), capitalize(segment));
                listItem.add(anchor);
                if(i == segments.length - 1) {
                    anchor.getStyle().set("color", "red");
                } else {
                    anchor.getStyle().set("color", "rgba(0, 0, 0, 0.6)");
                }
                breadcrumbList.add(listItem);
            }
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        // Aktualizacja breadcrumb
        String irl = UI.getCurrent().getInternals().getActiveViewLocation().getPath();
        updateBreadcrumb(irl);
        //viewTitle.setText(getCurrentPageTitle());

    }

    private String getCurrentPageTitle() {
        if (getContent() == null) {
            return "";
        } else if (getContent() instanceof HasDynamicTitle titleHolder) {
            return titleHolder.getPageTitle();
        } else {
            var title = getContent().getClass().getAnnotation(PageTitle.class);
            return title == null ? "" : title.value();
        }
    }

    private Component searchBar() {
        HorizontalLayout searchBar = new HorizontalLayout();
        ComboBox<String> searchBox = new ComboBox<>();
        Button searchButton = new Button(new Icon(VaadinIcon.SEARCH));
        searchBar.setSpacing(false);

        searchBox.setPlaceholder("Szukaj");
        searchBox.addClassName("no-arrow");
        searchBox.setWidth("300%");
        searchBox.getStyle().set("--vaadin-input-field-height","50px");

        searchButton.getStyle().set("--vaadin-button-height","50px");
        searchButton.setAriaLabel("Szukaj");

        searchBar.add(searchBox);
        searchBar.add(searchButton);
        return searchBar;
    }

    private Component breadcrumb() {
        breadcrumbNav = new Nav();
        breadcrumbNav.setClassName("breadcrumb");
        breadcrumbNav.setAriaLabel("Breadcrumb");

        breadcrumbList = new OrderedList();
        breadcrumbList.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FontSize.XXSMALL,
                LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.TextTransform.UPPERCASE);
        breadcrumbNav.add(breadcrumbList);
        return breadcrumbNav;
    }

    private Component menuBars(){
        Anchor podstrona1 = new Anchor("Podstrona1");
        podstrona1.setHref("podstrona");
        podstrona1.setText("Podstrona");

        MenuBar firstmenubar = new MenuBar();
        MenuItem shareMenuItem = firstmenubar.addItem("Zakladka 1");
        SubMenu shareSubMenu = shareMenuItem.getSubMenu();
        MenuItem onSocialMeMenuItem = shareSubMenu.addItem("Podzakladka 1");
        SubMenu onSocialMeSubMenu = onSocialMeMenuItem.getSubMenu();
        MenuItem facebookMenuItem = onSocialMeSubMenu.addItem("PodPozakladka 1");
        MenuItem twitterMenuItem = onSocialMeSubMenu.addItem("PodPodzakladka 2");
        MenuItem instagramMenuItem = onSocialMeSubMenu.addItem("PodPodzakladka 3");
        MenuItem byEmailMenuItem = shareSubMenu.addItem(podstrona1);
        MenuItem getLinkMenuItem = shareSubMenu.addItem("Podzakladka 3");

        HorizontalLayout menuBars = new HorizontalLayout();
        menuBars.add(firstmenubar);
        return menuBars;
    }
}
