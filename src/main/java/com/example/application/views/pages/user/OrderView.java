package com.example.application.views.pages.user;

import com.example.application.model.Item;
import com.example.application.security.SecurityService;
import com.example.application.service.SessionCartService;
import com.example.application.views.controllers.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Map;
import java.util.Optional;

@Route(value = "zamow", layout = MainLayout.class)
@AnonymousAllowed
public class OrderView extends HorizontalLayout {

    private final SecurityService securityService;
    private final SessionCartService sessionCartService;

    public OrderView(SecurityService securityService, SessionCartService sessionCartService){
        this.securityService = securityService;
        this.sessionCartService = sessionCartService;

        Long userId = securityService.getAuthenticatedUserId();

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

        Button kontynuuj = new Button("Podsumowanie");
        kontynuuj.addClickListener(click -> {

                }
        );
        kontynuuj.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);

        VerticalLayout orderFields = new VerticalLayout(imie,nazwisko,adres,kodpocztowy,miasto,telefon, kontynuuj);
        orderFields.setClassName("order-vertical-layout");
        add(orderFields);

        if(userId.equals(0L)){
            sessionOrder();
        } else {
            userOrder();
        }
    }

    public void sessionOrder(){

        Map<Integer, Integer> sessionCart = sessionCartService.getCart();

        Grid<Integer> cartGrid = new Grid<>();

        Div gridContainer = new Div();

        gridContainer.addClassName("my-grid-container");
        gridContainer.add(cartGrid);

        cartGrid.setAllRowsVisible(true);
        cartGrid.addClassName("cart-grid");

        cartGrid.addColumn(createCartItemSessionRenderer(sessionCartService))
                .setHeader("Produkty").setAutoWidth(true).setFlexGrow(1).setHeaderPartName("header");
        


    }

    private static Renderer<Integer> createCartItemSessionRenderer(SessionCartService sessionCartService) {
        return LitRenderer.<Integer>of(
                        "<vaadin-horizontal-layout style=\"align-items: center; width: 100%;\" theme=\"spacing\">"
                                + "  <img src=\"${item.pictureUrl}\" alt=\"Item image\" style=\"width: 50px; height: 50px; border-radius: 50%; margin: 0;\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m); flex-grow: 1;\">"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.fullName}"
                                + "    </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.id}"
                                + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "  <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-left: auto; text-align: right;\">"
                                + "    ${item.price}"
                                + "  </span>"
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

    public void userOrder(){

    }
}
