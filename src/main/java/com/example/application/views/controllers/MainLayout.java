package com.example.application.views.controllers;

import com.example.application.repository.ItemRepository;
import com.example.application.security.SecurityService;
import com.example.application.service.SearchService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

@AnonymousAllowed
public class MainLayout extends AppLayout {

    private Nav breadcrumbNav;
    private OrderedList breadcrumbList;
    private SecurityService securityService;
    private ItemRepository itemRepository;


    public MainLayout(@Autowired SecurityService securityService, @Autowired ItemRepository itemRepository, @Autowired SearchService searchService) {
        this.securityService = securityService;

        HorizontalLayout searchBarLayout = searchService.createSearchBar();

        Component user;
        if (securityService.getAuthenticatedUser() != null) {
            user = loggedUserMenu(true);
        } else {
            user = loggedUserMenu(false);
        }

        addNavbarContent(user, searchBarLayout);
    }


    private void addNavbarContent(Component userButton, HorizontalLayout searchBarLayout) {

        Icon logo = new Icon();
        logo.setIcon(VaadinIcon.CLOUD_DOWNLOAD);
        logo.setSize("100px");

        Anchor loginLink = iconAnchor("ZALOGUJ","/login", VaadinIcon.USER);
        Anchor loginLink2 = iconAnchor("KOSZYK","/podstrona", VaadinIcon.CART);
        Anchor loginLink3 = iconAnchor("SCHOWEK","/p3", VaadinIcon.BARCODE);
        Anchor loginLink4 = iconAnchor("KONTAKT","/podstrona/podstrona2", VaadinIcon.CHAT);

        //Button loginButton = accountButton();

        Div userInterfaceLeft = new Div();
        userInterfaceLeft.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Padding.Horizontal.XLARGE, LumoUtility.Gap.XLARGE);
        userInterfaceLeft.add(userButton);
        userInterfaceLeft.add(loginLink2);


        Div userInterfaceRight = new Div();
        userInterfaceRight.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Padding.Horizontal.XLARGE, LumoUtility.Gap.XLARGE);
        userInterfaceRight.add(loginLink3);
        userInterfaceRight.add(loginLink4);

        Div userInterfaceRoot = new Div();
        userInterfaceRoot.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
        userInterfaceRoot.add(userInterfaceLeft);
        userInterfaceRoot.add(userInterfaceRight);

        HorizontalLayout navbar = new HorizontalLayout();
        navbar.setWidth("80%");
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbar.add(logo,searchBarLayout,userInterfaceRoot);
        navbar.setFlexGrow(1,searchBarLayout);
        navbar.addClassNames(LumoUtility.Padding.Vertical.NONE);

        VerticalLayout menuBar = new VerticalLayout();
        menuBar.add(navbar,menuBars(),breadcrumb());
        menuBar.setAlignItems(FlexComponent.Alignment.CENTER);

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

    /*private Component searchBar(ItemRepository itemRepository) {

        this.itemRepository = itemRepository;
        // Upewnij się, że itemRepository nie jest null
        if (itemRepository == null) {
            throw new IllegalStateException("itemRepository is null");
        }

        HorizontalLayout searchBar = new HorizontalLayout();
        searchBar.setWidth("30%");
        ComboBox<Item> searchBox = new ComboBox<>();
        Button searchButton = new Button(new Icon(VaadinIcon.SEARCH));
        searchBar.setSpacing(false);

        searchBox.setPlaceholder("Szukaj");
        searchBox.addClassName("no-arrow");
        searchBox.setWidth("300%");
        searchBox.getStyle().set("--vaadin-input-field-height","50px");
        searchBox.setItemLabelGenerator(Item::getName);
        searchBox.setItems(query -> {
            String filter = query.getFilter().orElse("");  // Uzyskanie tekstu filtru
            return itemRepository.findByNameContainingIgnoreCase(filter).stream();  // Zwraca Stream<Item>
        });

        searchButton.getStyle().set("--vaadin-button-height","50px");
        searchButton.setAriaLabel("Szukaj");

        searchBar.add(searchBox);
        searchBar.add(searchButton);
        searchBar.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER, LumoUtility.Padding.Vertical.XSMALL);
        return searchBar;
    }*/

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
        MenuBar mainMenu = new MenuBar();
        mainMenu.setOpenOnHover(true);
        mainMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);


        MenuItem elektronika = createIconItem(mainMenu,VaadinIcon.BED,"Elektronika",null);
        MenuItem smartHome = createIconItem(mainMenu,VaadinIcon.HOME,"SmartHome",null);

        MenuItem druk3D = createIconItem(mainMenu,VaadinIcon.AMBULANCE,"Druk 3D",null);

        SubMenu firstItemSubMenu = druk3D.getSubMenu();
        MenuItem test = firstItemSubMenu.addItem("Podstrona 1");
        test.addClickListener(click -> UI.getCurrent().navigate("/reg"));


        SubMenu testSubMenu = test.getSubMenu();
        MenuItem test2 = testSubMenu.addItem("Podstrona 2");
        test2.addClickListener(click -> UI.getCurrent().navigate("/p3"));

        return mainMenu;
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
                                    String label, String ariaLabel) {
        Icon icon = new Icon(iconName);
        icon.getStyle().set("width", "var(--lumo-icon-size-m)");
        icon.getStyle().set("height", "var(--lumo-icon-size-m)");
        icon.getStyle().set("marginRight", "var(--lumo-space-s)");

        MenuItem item = menu.addItem(icon, e -> {
        });

            item.add(new Text(" " + label));

        return item;
    }

    private Anchor iconAnchor(String text, String url, VaadinIcon iconName){
        Icon icon = new Icon(iconName);
        Span tekst = new Span(text);
        tekst.getElement().getStyle().set("font-size","20px");
        Anchor anchor = new Anchor(url, "");
        anchor.addClassName("icon_link");
        anchor.add(icon, tekst);
        return anchor;
    }

    private MenuBar loggedUserMenu(boolean logged){
        MenuBar mainMenu = new MenuBar();
        mainMenu.setOpenOnHover(true);
        mainMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem userAll = createIconItem(mainMenu, VaadinIcon.USER, "KONTO", null);


        if (logged) {
            userAll.addClickListener(click2 -> UI.getCurrent().navigate("/reg"));
            SubMenu logout = userAll.getSubMenu();
            logout.addItem("Zamowienia");
            logout.addItem("Dane");
            logout.addItem("Ulubione");
            logout.addItem("Wyloguj sie").addClickListener(click -> securityService.logout());
        } else {
            userAll.addClickListener(click2 -> UI.getCurrent().navigate("/login"));
        }

        return mainMenu;
    }
}
