package com.example.application.views.controllers;

import com.example.application.model.Cart;
import com.example.application.model.CartItem;
import com.example.application.model.Category;
import com.example.application.model.Item;
import com.example.application.repository.CartItemRepository;
import com.example.application.repository.CartRepository;
import com.example.application.repository.CategoryRepository;
import com.example.application.security.SecurityService;
import com.example.application.service.CartService;
import com.example.application.service.SearchService;
import com.example.application.service.SessionCartService;
import com.example.application.updateevents.CartUpdatedEvent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainCustomLayout extends VerticalLayout implements RouterLayout, BeforeEnterObserver {

    private Nav breadcrumbNav;
    private OrderedList breadcrumbList;
    private final SecurityService securityService;
    private final CategoryRepository categoryRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final SessionCartService sessionCartService;
    private final SearchService searchService;
    private Map<CartItem, Integer> temporaryQuantities = new HashMap<>();
    private MenuItem userAll;
    private SubMenu cartitems;
    private MultiSelectListBox<CartItem> itemList;
    MultiSelectListBox<Integer> sessionItems;
    Div gridContainer = new Div();

    private VerticalLayout contentContainer; // Kontener na widok
    private HorizontalLayout header;         // Nawigacja
    private HorizontalLayout footer;         // Stopka


    public MainCustomLayout(SecurityService securityService, CategoryRepository categoryRepository, CartService cartService, CartRepository cartRepository, CartItemRepository cartItemRepository, SessionCartService sessionCartService, SearchService searchService) {
        this.securityService = securityService;
        this.categoryRepository = categoryRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.sessionCartService = sessionCartService;
        this.searchService = searchService;

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
addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER);

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

        navbar.addClassNames(LumoUtility.Padding.Vertical.NONE);

        VerticalLayout menuBar = new VerticalLayout();
        menuBar.add(navbar, menuBars(), breadcrumb());
        menuBar.setAlignItems(FlexComponent.Alignment.CENTER);
        menuBar.setWidth("60%");
        menuBar.addClassNames(LumoUtility.Gap.XSMALL);

        header = new HorizontalLayout(menuBar);

        contentContainer = new VerticalLayout();
        contentContainer.setWidthFull();
        contentContainer.setAlignItems(Alignment.CENTER);
        contentContainer.setPadding(false);
        contentContainer.setSpacing(false);

        //footer();

        header.setWidthFull();
        header.addClassName("custom-horizontal-layout");
        add(header, contentContainer);
        setFlexGrow(1, contentContainer);
    }

    private void footer(){
        footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.addClassName("footer-horizontal-layout");
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN); // Rozdziela elementy na końce
        footer.setAlignItems(Alignment.CENTER); // Wyrównuje elementy w pionie

        VerticalLayout kontakt = new VerticalLayout();
        VerticalLayout produkty = new VerticalLayout();
        VerticalLayout informacje = new VerticalLayout();
        VerticalLayout osklepie = new VerticalLayout();
        VerticalLayout konto = new VerticalLayout();

        kontakt.addClassName("footer-horizontal-layout a");
        kontakt.add(new H3("KONTAKT"), new Span("+48 0700 2137 69"), new Span("Napisz do nas"), new Span("Adres"));
        produkty.add(new H3("PRODUKTY"), new Span("Promocje"), new Span("Nowe produkty"), new Span("Najczesciej kupowane"));
        informacje.add(new H3("INFORMACJE"), new Span("Reklamacje"), new Span("Regulamin"), new Span("Dostawcy"));
        osklepie.add(new H3("O SKLEPIE"), new Span("O nas"), new Span("Opinie"), new Span("Newsletter"));
        konto.add(new H3("KONTO"), new Span("Dane osobowe"), new Span("Adresy"), new Span("Zamowienia"));

        // Dodanie elementów do stopki
        footer.add(kontakt,produkty,informacje,osklepie,kontakt);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        contentContainer.getElement().removeAllChildren(); // Czyści istniejące widoki
        if (content != null) {
            contentContainer.getElement().appendChild(content.getElement());
        }
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
        mainMenu.addClassName("unique-menubar");
        // mainMenu.setOpenOnHover(true);
        mainMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);


        if (userId != 0L) {
            removeAll();
            Cart cart = cartRepository.findByUserId(userId);
            List<CartItem> itemsInCart = cartItemRepository.findByCartId(cart.getId());

            Grid<CartItem> cartButtonGrid = new Grid<>(CartItem.class, false);
            cartButtonGrid.addClassName("cart-grid");
            cartButtonGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER,GridVariant.LUMO_NO_ROW_BORDERS,GridVariant.LUMO_COMPACT);
            cartButtonGrid.setAllRowsVisible(true);
            cartButtonGrid.addColumn(createCartItemRenderer())
                    .setAutoWidth(true).setFlexGrow(1).setHeader("Produkty").setHeaderPartName("header");

            cartButtonGrid.setItems(itemsInCart);

            gridContainer.addClassName("my-grid-container");
            gridContainer.add(cartButtonGrid);



            itemList = cartService.multiitembox(itemsInCart, temporaryQuantities, cart);
            userAll = createIconItem(mainMenu, VaadinIcon.CART, "KOSZYK ( " + itemsInCart.size() + " )", null);
            userAll.setCheckable(false);
            cartitems = userAll.getSubMenu();

            cartitems.addItem(gridContainer).addClassNames(LumoUtility.Gap.XSMALL,LumoUtility.Margin.NONE,LumoUtility.Background.TRANSPARENT,LumoUtility.Padding.NONE);

        } else {
            removeAll();
            Map<Integer, Integer> cart = sessionCartService.getCart();

            Grid<Integer> cartGrid = new Grid<>();

            cartGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER,GridVariant.LUMO_NO_ROW_BORDERS,GridVariant.LUMO_COMPACT);
            cartGrid.setAllRowsVisible(true);
            cartGrid.addClassName("cart-grid");
            cartGrid.addColumn(createCartItemSessionRenderer(sessionCartService))
                    .setAutoWidth(true).setFlexGrow(1).setHeader("Produkty").setHeaderPartName("header");

            cartGrid.setItems(cart.keySet());

            gridContainer.addClassName("my-grid-container");
            gridContainer.add(cartGrid);
            //sessionItems = cartService.multiitemboxForSessionCart(sessionCartService, cart);
            userAll = createIconItem(mainMenu, VaadinIcon.CART, "KOSZYK ( " + cart.size() + " )", null);
            userAll.setCheckable(false);
            cartitems = userAll.getSubMenu();
            cartitems.addItem(gridContainer).addClassNames(LumoUtility.Gap.XSMALL,LumoUtility.Margin.NONE,LumoUtility.Background.TRANSPARENT,LumoUtility.Padding.NONE);

        }


        userAll.addClickListener(click2 -> UI.getCurrent().navigate("koszyk"));

        return mainMenu;
    }

    private static Renderer<Integer> createCartItemSessionRenderer(SessionCartService sessionCartService) {
        return LitRenderer.<Integer>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <img src=\"${item.pictureUrl}\" alt=\"Item image\" style=\"width: 50px; height: 50px; border-radius: 50%; margin-right: var(--lumo-space-m);\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.fullName}"
                                + "    </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.id}"
                                + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.price}"
                                + "    </span>"
                                + "</vaadin-horizontal-layout>"
                )
                .withProperty("pictureUrl", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    return optionalItem.get().getImageUrl();
                }) // URL do obrazu
                .withProperty("fullName", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    return optionalItem.get().getName();
                })      // Nazwa przedmiotu
                .withProperty("id", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    return "Indeks: " + optionalItem.get().getId();
                }) // Indeks z dodanym napisem
                .withProperty("price", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    Map<Integer, Integer> cart = sessionCartService.getCart();
                    return optionalItem.get().getPrice() * cart.get(cartItem);
                });
    }

    private static Renderer<CartItem> createCartItemRenderer() {
        return LitRenderer.<CartItem>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <img src=\"${item.pictureUrl}\" alt=\"Item image\" style=\"width: 50px; height: 50px; border-radius: 50%; margin-right: var(--lumo-space-m);\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.fullName}"
                                + "    </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.id}"
                                + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.price}"
                                + "    </span>"
                                + "</vaadin-horizontal-layout>"
                )
                .withProperty("pictureUrl", cartItem -> cartItem.getItem().getImageUrl()) // URL do obrazu
                .withProperty("fullName", cartItem -> cartItem.getItem().getName())      // Nazwa przedmiotu
                .withProperty("id", cartItem -> "Indeks: " + cartItem.getItem().getId())
                .withProperty("price", cartItem -> cartItem.getQuantity() * cartItem.getItem().getPrice()); // Indeks z dodanym napisem
    }

    public void updateCartIcon(Long userId) {
        if (userId != 0L) {
            Cart cart = cartRepository.findByUserId(userId);
            List<CartItem> itemsInCart = cartItemRepository.findByCartId(cart.getId());
            updateTextInMenuItem(userAll,"KOSZYK ( " + itemsInCart.size() + " )");
            cartitems.removeAll();
            cartitems.addItem(gridContainer).addClassNames(LumoUtility.MaxWidth.SCREEN_SMALL);

        } else {
            Map<Integer, Integer> cart = sessionCartService.getCart();
            updateTextInMenuItem(userAll,"KOSZYK ( " + cart.size() + " )");
            MultiSelectListBox<Integer> sessionItems = cartService.multiitemboxForSessionCart(sessionCartService, cart);
            cartitems.removeAll();
            cartitems.addItem(gridContainer).addClassName(LumoUtility.MaxWidth.SCREEN_SMALL);
        }
    }

    public void updateTextInMenuItem(MenuItem item, String newLabel) {
        item.getChildren()
                .filter(child -> child instanceof Text)
                .map(child -> (Text) child)
                .findFirst() // Znajdź pierwszy element typu Text
                .ifPresent(text -> text.setText(newLabel)); // Zaktualizuj tekst
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        updateBreadcrumb(beforeEnterEvent.getLocation().getPath());
    }
}
