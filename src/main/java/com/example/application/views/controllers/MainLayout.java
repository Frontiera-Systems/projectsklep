package com.example.application.views.controllers;

import com.example.application.model.Cart;
import com.example.application.model.CartItem;
import com.example.application.model.Category;
import com.example.application.repository.*;
import com.example.application.security.SecurityService;
import com.example.application.service.CartService;
import com.example.application.service.SearchService;
import com.example.application.service.SessionCartService;
import com.example.application.updateevents.CartUpdatedEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AnonymousAllowed
public class MainLayout extends AppLayout implements RouterLayout, BeforeEnterObserver {

    private Nav breadcrumbNav;
    private OrderedList breadcrumbList;
    private final SecurityService securityService;
    private final CategoryRepository categoryRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final SessionCartService sessionCartService;
    private Map<CartItem, Integer> temporaryQuantities = new HashMap<>();
    private MenuItem userAll;
    private SubMenu cartitems;
    private MultiSelectListBox<CartItem> itemList;
    MultiSelectListBox<Integer> sessionItems;


    public MainLayout(@Autowired SecurityService securityService, @Autowired SearchService searchService, CategoryRepository categoryRepository, UserRepository userRepository, CartService cartService, ItemRepository itemRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, SessionCartService sessionCartService) {
        this.securityService = securityService;
        this.categoryRepository = categoryRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.sessionCartService = sessionCartService;

        HorizontalLayout searchBarLayout = searchService.createSearchBar();

        Component user;
        Component cart;


        Long userId = securityService.getAuthenticatedUserId();

        ComponentUtil.addListener(UI.getCurrent(), CartUpdatedEvent.class, event -> {
            updateCartIcon(event.getUserId());
        });

        if (userId != 0L) {
            user = loggedUserMenu(true);
        } else {
            user = loggedUserMenu(false);
        }
        cart = cartButton(userId);

        setClassName("custom-navbar");
        addNavbarContent(user, cart, searchBarLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Aktualizuj breadcrumb za każdym razem, gdy użytkownik odwiedza nową stronę
        updateBreadcrumb(event.getLocation().getPath());
    }


    private void addNavbarContent(Component userButton, Component cartButton, HorizontalLayout searchBarLayout) {

        Icon logo = new Icon();
        logo.setIcon(VaadinIcon.CLOUD_DOWNLOAD);
        logo.setSize("100px");

        Image logoImage = new Image("https://u.cubeupload.com/korylek/looongcat.png","");
        logoImage.setHeight("100px");

        Anchor loginLink = iconAnchor("ZALOGUJ", "/login", VaadinIcon.USER);
        Anchor loginLink4 = iconAnchor("KONTAKT", "/podstrona/podstrona2", VaadinIcon.CHAT);

        //Button loginButton = accountButton();

        Div userInterfaceLeft = new Div();
        userInterfaceLeft.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Padding.Horizontal.XLARGE, LumoUtility.Gap.XLARGE);
        userInterfaceLeft.add(userButton);
        //userInterfaceLeft.add(loginLink2);


        Div userInterfaceRight = new Div();
        userInterfaceRight.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Padding.Horizontal.XLARGE, LumoUtility.Gap.XLARGE);
        // userInterfaceRight.add(loginLink3);
        userInterfaceRight.add(cartButton);

        Div userInterfaceRoot = new Div();
        userInterfaceRoot.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
        userInterfaceRoot.add(userInterfaceLeft);
        userInterfaceRoot.add(userInterfaceRight);

        HorizontalLayout navbar = new HorizontalLayout();
        navbar.setWidth("80%");
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbar.add(logoImage, searchBarLayout, userInterfaceRoot);

        // navbar.setFlexGrow(1,searchBarLayout);
        navbar.addClassNames(LumoUtility.Padding.Vertical.NONE);

        VerticalLayout menuBar = new VerticalLayout();
        menuBar.add(navbar, menuBars(), breadcrumb());
        menuBar.setAlignItems(FlexComponent.Alignment.CENTER);
        menuBar.setWidth("60%");
        menuBar.addClassNames(LumoUtility.Gap.XSMALL);

        HorizontalLayout rootLayout = new HorizontalLayout(menuBar);
        rootLayout.setWidthFull();
       /* rootLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        rootLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);*/
        rootLayout.addClassName("custom-horizontal-layout");
        addToNavbar(rootLayout);
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
                if (i == segments.length - 1) {
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

    private Component menuBars() {
        MenuBar mainMenu = new MenuBar();
        mainMenu.setOpenOnHover(true);
        mainMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);

        List<Category> parentCategories = categoryRepository.findByParentIsNull();
        parentCategories.forEach(category -> {
            MenuItem t = createIconItem(mainMenu, VaadinIcon.valueOf(category.getImageUrl()), category.getName(), null);
            dynamiMenuBar(category, t);
        });
        return mainMenu;
    }

    private void dynamiMenuBar(Category category, MenuItem menuItem) {
        List<Category> kid = category.getSubcategories();
        menuItem.addClickListener(click -> {
            UI.getCurrent().navigate(category.getFullPath());
        });
        if (!kid.isEmpty()) {
            SubMenu submenu = menuItem.getSubMenu();
            kid.forEach(kids -> {
                MenuItem kidSub = submenu.addItem(kids.getName());
                dynamiMenuBar(kids, kidSub);
            });
        }
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

    private Anchor iconAnchor(String text, String url, VaadinIcon iconName) {
        Icon icon = new Icon(iconName);
        Span tekst = new Span(text);
        tekst.getElement().getStyle().set("font-size", "20px");
        Anchor anchor = new Anchor(url, "");
        anchor.addClassName("icon_link");
        anchor.add(icon, tekst);
        return anchor;
    }

    private MenuBar loggedUserMenu(boolean logged) {
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

    private MenuBar cartButton(Long userId) {
        MenuBar mainMenu = new MenuBar();
       // mainMenu.setOpenOnHover(true);
        mainMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);


        if (userId != 0L) {
            Cart cart = cartRepository.findByUserId(userId);
            List<CartItem> itemsInCart = cartItemRepository.findByCartId(cart.getId());
            itemList = cartService.multiitembox(itemsInCart, temporaryQuantities, cart);
            userAll = createIconItem(mainMenu, VaadinIcon.CART, "KOSZYK ( " + itemsInCart.size() + " )", null);
            cartitems = userAll.getSubMenu();
            cartitems.addItem(itemList).addClassNames(LumoUtility.MaxWidth.SCREEN_SMALL);

        } else {
            Map<Integer, Integer> cart = sessionCartService.getCart();
            sessionItems = cartService.multiitemboxForSessionCart(sessionCartService, cart);
            userAll = createIconItem(mainMenu, VaadinIcon.CART, "KOSZYK ( " + cart.size() + " )", null);
            cartitems = userAll.getSubMenu();
            cartitems.addItem(sessionItems).addClassName(LumoUtility.MaxWidth.SCREEN_SMALL);

        }


        userAll.addClickListener(click2 -> UI.getCurrent().navigate("koszyk"));

        return mainMenu;
    }

    public void updateCartIcon(Long userId) {
        if (userId != 0L) {
            Cart cart = cartRepository.findByUserId(userId);
            List<CartItem> itemsInCart = cartItemRepository.findByCartId(cart.getId());
            updateTextInMenuItem(userAll,"KOSZYK ( " + itemsInCart.size() + " )");
            cartitems.removeAll();
            cartitems.addItem(itemList).addClassNames(LumoUtility.MaxWidth.SCREEN_SMALL);

        } else {
            Map<Integer, Integer> cart = sessionCartService.getCart();
            updateTextInMenuItem(userAll,"KOSZYK ( " + cart.size() + " )");
            MultiSelectListBox<Integer> sessionItems = cartService.multiitemboxForSessionCart(sessionCartService, cart);
            cartitems.removeAll();
            cartitems.addItem(sessionItems).addClassName(LumoUtility.MaxWidth.SCREEN_SMALL);
        }
    }

    public void updateTextInMenuItem(MenuItem item, String newLabel) {
        item.getChildren()
                .filter(child -> child instanceof Text)
                .map(child -> (Text) child)
                .findFirst() // Znajdź pierwszy element typu Text
                .ifPresent(text -> text.setText(newLabel)); // Zaktualizuj tekst
    }


}
