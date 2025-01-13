package com.example.application.views.pages.user;

import com.example.application.model.CartItem;
import com.example.application.security.SecurityService;
import com.example.application.views.controllers.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "zamow", layout = MainLayout.class)
@AnonymousAllowed
public class OrderView extends HorizontalLayout {

    private final SecurityService securityService;
    public OrderView(SecurityService securityService){
        this.securityService = securityService;
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

    private void sessionOrder(){
        @SuppressWarnings("unchecked")
        Set<Integer> selectedItems = (Set<Integer>) UI.getCurrent().getSession().getAttribute("selectedItems");

        // Jeśli brak danych, ustaw pusty zestaw
        if (selectedItems == null) {
            selectedItems = Collections.emptySet();
        }

        Grid<Integer> grid = new Grid<>();
        grid.addColumn(item -> item).setHeader("Selected Items"); // Kolumna wyświetlająca wartości
        grid.setItems(selectedItems);
        add(grid);
    }

    private void userOrder(){
        @SuppressWarnings("unchecked")
        Set<CartItem> selectedItems = (Set<CartItem>) UI.getCurrent().getSession().getAttribute("selectedItems");

        // Jeśli brak danych, ustaw pusty zestaw
        if (selectedItems == null || selectedItems.isEmpty()) {
            add(new Span("Brak wybranych przedmiotów."));
            return;
        }

        // Tworzenie Grid
        Grid<SummaryItem> grid = new Grid<>(SummaryItem.class);
        grid.setColumns("itemName", "totalPrice");

        // Przetwarzanie danych do podsumowania
        List<SummaryItem> summaryItems = selectedItems.stream()
                .map(cartItem -> new SummaryItem(
                        cartItem.getItem().getName(),
                        cartItem.getQuantity() * cartItem.getItem().getPrice()
                ))
                .collect(Collectors.toList());

        grid.setItems(summaryItems);

        // Wyświetlanie Grid
        add(grid);
    }

    public static class SummaryItem {
        private String itemName;
        private double totalPrice;

        public SummaryItem(String itemName, double totalPrice) {
            this.itemName = itemName;
            this.totalPrice = totalPrice;
        }

        public String getItemName() {
            return itemName;
        }

        public double getTotalPrice() {
            return totalPrice;
        }
    }

}
