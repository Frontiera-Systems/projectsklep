package com.example.application.views.pages.user;

import com.example.application.views.controllers.MainLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "zamow", layout = MainLayout.class)
@AnonymousAllowed
public class OrderView extends HorizontalLayout {

    public OrderView(){
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
        telefon.setPattern("\\d{3}\\d{3}\\d{3}");
        telefon.setMinLength(9);
        telefon.setMaxLength(9);
        telefon.setI18n(new TextField.TextFieldI18n()
                .setRequiredErrorMessage("Pole wymagane!")
                .setMinLengthErrorMessage("Bledny numer telefonu")
                .setMaxLengthErrorMessage("Bledny numer telefonu")
                .setPatternErrorMessage("Bledny numer telefonu"));
        VerticalLayout orderFields = new VerticalLayout(imie,nazwisko,adres,kodpocztowy,miasto,telefon);
        //orderFields.setClassName("order-vertical-layout");
        add(orderFields);
    }
}
