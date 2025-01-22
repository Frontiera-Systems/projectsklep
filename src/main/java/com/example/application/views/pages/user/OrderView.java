package com.example.application.views.pages.user;

import com.example.application.model.*;
import com.example.application.repository.*;
import com.example.application.security.SecurityService;
import com.example.application.service.CartService;
import com.example.application.service.SessionCartService;
import com.example.application.views.controllers.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.*;

@Route(value = "zamow", layout = MainLayout.class)
@AnonymousAllowed
public class OrderView extends HorizontalLayout {

    private final SecurityService securityService;
    private final SessionCartService sessionCartService;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final CartService cartService;
    Button kontynuuj = new Button("Złóż zamówienie");
    Long userId;
    RadioButtonGroup<Map.Entry<String, Integer>> radioGroup = new RadioButtonGroup<>();

    public OrderView(SecurityService securityService, SessionCartService sessionCartService, CartItemRepository cartItemRepository, CartRepository cartRepository, UserRepository userRepository, OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository, CartService cartService, ItemRepository itemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.cartService = cartService;
        this.securityService = securityService;
        this.sessionCartService = sessionCartService;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        userId = securityService.getAuthenticatedUserId();

        TextField imie = new TextField("Imie");
        imie.setRequiredIndicatorVisible(true);

        TextField nazwisko = new TextField("Nazwisko");
        nazwisko.setRequiredIndicatorVisible(true);

        TextField adres = new TextField("Adres");
        adres.setRequiredIndicatorVisible(true);

        TextField kodpocztowy = new TextField("Kod pocztowy");
        kodpocztowy.setRequiredIndicatorVisible(true);
        kodpocztowy.setPattern("\\d{2}-\\d{3}");
        kodpocztowy.setAllowedCharPattern("[0-9-]");
        kodpocztowy.setI18n(new TextField.TextFieldI18n()
                .setRequiredErrorMessage("Pole wymagane!")
                .setMinLengthErrorMessage("Bledny kod pocztowy")
                .setMaxLengthErrorMessage("Bledny kod pocztowy")
                .setPatternErrorMessage("Bledny kod pocztowy"));


        TextField miasto = new TextField("Miasto");
        miasto.setRequiredIndicatorVisible(true);

        TextField telefon = new TextField("Numer telefonu");
        telefon.setRequiredIndicatorVisible(true);
        telefon.setPattern("\\d{9}");
        telefon.setAllowedCharPattern("[0-9]");
        telefon.setMinLength(9);
        telefon.setMaxLength(9);
        telefon.setI18n(new TextField.TextFieldI18n()
                .setRequiredErrorMessage("Pole wymagane!")
                .setMinLengthErrorMessage("Bledny numer telefonu")
                .setMaxLengthErrorMessage("Bledny numer telefonu")
                .setPatternErrorMessage("Bledny numer telefonu"));



        kontynuuj.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);

        VerticalLayout orderFields = new VerticalLayout(imie, nazwisko, adres, kodpocztowy, miasto, telefon);
        orderFields.setClassName("order-vertical-layout");
        orderFields.getStyle().setWidth("20%");
        add(orderFields);

        if (userId.equals(0L)) {
            sessionOrder();
            kontynuuj.addClickListener(click -> {

                        });
        } else {
            User user = userRepository.findById(userId);
            Cart cart = cartRepository.findByUserId(user.getId());
            kontynuuj.addClickListener(click -> {
                Order order= new Order();
                order.setKurier(radioGroup.getValue().getKey());
                order.setImie(imie.getValue());
                order.setNazwisko(nazwisko.getValue());
                order.setAdres(adres.getValue());
                order.setUser(user);
                order.setTelefon(telefon.getValue());
                order.setMiasto(miasto.getValue());
                order.setKodpocztowy(kodpocztowy.getValue());
                orderRepository.save(order);
                List<OrderDetails> orderDetailsList = new ArrayList<>();
                List<CartItem> items = cart.getItems();
                items.forEach(item -> {
                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setOrder(order);
                    orderDetails.setItem(item.getItem());
                    orderDetails.setQuantity(item.getQuantity());
                    orderDetails.setPrice(item.getItem().getPrice());
                    orderDetailsRepository.save(orderDetails);
                    orderDetailsList.add(orderDetails);
                    cart.removeItem(item);
                });
                order.setOrderDetails(orderDetailsList);
                orderRepository.save(order);

                Dialog pokwitowanie = new Dialog();
                pokwitowanie.setHeaderTitle("ZAMOWIENIE ZLOZONE, ID: "+ order.getId());
                pokwitowanie.open();
            });
            userOrder(cart);
        }

    }

    public void sessionOrder() {

        Map<Integer, Integer> sessionCart = sessionCartService.getCart();

        Grid<Integer> cartGrid = new Grid<>();
        cartGrid.setAllRowsVisible(true);
        cartGrid.addClassName("cart-grid");
        cartGrid.setItems(sessionCart.keySet());
        cartGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COMPACT);
        cartGrid.addColumn(createCartItemSessionRenderer(sessionCartService))
                .setHeader("Produkty").setAutoWidth(true).setFlexGrow(1).setHeaderPartName("header").setFooter(createTotalPriceFooter(sessionCartService));
        cartGrid.setItems(sessionCart.keySet());

        VerticalLayout orderFields2 = new VerticalLayout(cartGrid, shipment(),kontynuuj);
        orderFields2.setClassName("order-vertical-layout");
        orderFields2.getStyle().setWidth("20%");
        orderFields2.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.CENTER);
        add(orderFields2);
    }

    private static Renderer<Integer> createCartItemSessionRenderer(SessionCartService sessionCartService) {
        return LitRenderer.<Integer>of(
                        "<vaadin-horizontal-layout style=\"align-items: center; width: 100%;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m); flex-grow: 1;\">"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.fullName}"
                                + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "  <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-left: auto; text-align: right;\">"
                                + "    ${item.price}"
                                + "  </span>"
                                + "</vaadin-horizontal-layout>"
                )
                .withProperty("fullName", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    return optionalItem.get().getName();
                })      // Nazwa przedmiotu
                .withProperty("price", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    Map<Integer, Integer> cart = sessionCartService.getCart();
                    return String.format("%.2f zł", optionalItem.get().getPrice() * cart.get(cartItem));
                });
    }

    private static String createTotalPriceFooter(SessionCartService sessionCartService) {

        return String.format("Całość: %.2f zł", sessionCartService.getTotalCartPrice());
    }

    public void userOrder(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        Grid<CartItem> cartGrid = new Grid<>(CartItem.class, false);

        Div gridContainer = new Div();
        gridContainer.addClassName("my-grid-container");
        gridContainer.add(cartGrid);

        cartGrid.setAllRowsVisible(true);
        cartGrid.addClassName("cart-grid");
        cartGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COMPACT);

        cartGrid.addColumn(createCartItemRenderer())
                .setHeader("Produkty").setAutoWidth(true).setFlexGrow(1).setHeaderPartName("header").setFooter(createTotalPriceFooter(cartService,userId));

        cartGrid.setItems(cartItems);

        VerticalLayout orderFields2 = new VerticalLayout(cartGrid, shipment(),kontynuuj);
        orderFields2.setClassName("order-vertical-layout");
        orderFields2.getStyle().setWidth("20%");
        orderFields2.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.CENTER);
        add(orderFields2);

    }

    private static String createTotalPriceFooter(CartService cartService, Long userID) {

        return String.format("Całość: %.2f zł", cartService.calculateTotalCartValue(userID));
    }

    private static Renderer<CartItem> createCartItemRenderer() {
        return LitRenderer.<CartItem>of(
                        "<vaadin-horizontal-layout style=\"align-items: center; width: 100%;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m); flex-grow: 1;\">"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.fullName}"
                                + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "  <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-left: auto; text-align: right;\">"
                                + "    ${item.price}"
                                + "  </span>"
                                + "</vaadin-horizontal-layout>"
                )// URL do obrazu
                .withProperty("fullName", cartItem -> cartItem.getItem().getName())
                .withProperty("price",cartItem -> String.format("%.2f zł", cartItem.getItem().getPrice() * cartItem.getQuantity()));// Nazwa przedmiotu
    }

    public Component shipment() {
        Map<String, Integer> listaPrzewoznikow = new HashMap<>();
        int[] ceny = {10, 20, 15};
        String[] nazwy = {"Inpost", "Dhl", "DPD"};

        for (int i = 0; i < nazwy.length; i++) {
            listaPrzewoznikow.put(nazwy[i], ceny[i]);
        }


        radioGroup.setItems(listaPrzewoznikow.entrySet());
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        radioGroup.setRenderer(new ComponentRenderer<>(przewoznik -> {
            Span number = new Span(new Text(przewoznik.getKey()));
            Text expiryDate = new Text(przewoznik.getValue() + " zł");

            return new Div(new HorizontalLayout(number, expiryDate));
        }));

        return radioGroup;
    }
}
