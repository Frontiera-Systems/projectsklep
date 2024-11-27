package com.example.application.security;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
@Component
public class RegistrationForm extends FormLayout {

    private H1 title;

    private TextField username;

    private PasswordField password;
    private PasswordField passwordConfirm;

    private Span errorMessageField;

    private Button submitButton;


    public RegistrationForm() {
        title = new H1("REJESTRACJA");
        Div spacer = new Div();
        username = new TextField("Login");

        password = new PasswordField("Hasło");
        passwordConfirm = new PasswordField("Powtórz hasło");

        password.setEnabled(true);
        password.setReadOnly(false);

        setRequiredIndicatorVisible(username,password,
                passwordConfirm);

        errorMessageField = new Span();

        submitButton = new Button("DOŁĄCZ");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Html loginLink = new Html("<a href='login' style='color: var(--lumo-primary-color); text-decoration: none;'>Masz już konto? Zaloguj się tutaj!</a>");

        add(title,username, password,
                passwordConfirm, errorMessageField,
                submitButton,loginLink);



        setMaxWidth("250px");

        setResponsiveSteps(
                new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));


        setColspan(title, 2);
        setColspan(errorMessageField, 2);
        setColspan(spacer,2);
        setColspan(submitButton, 2);

        getStyle().set("transform", "scale(1.1)") // Zwiększenie o 20%
                .set("transform-origin", "top")  // Ustawienie punktu odniesienia transformacji na górę
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center");  // Wycentrowanie formularza

        // Wycentrowanie napisu "Rejestracja"
        title.getStyle().set("text-align", "center");

        submitButton.getStyle().set("margin-top", "20px")
                            .set("margin-bottom","20px");

        loginLink.getStyle().set("text-align", "center");
    }

    public TextField getUsernameField() { return username;}

    public PasswordField getPasswordField() { return password; }

    public PasswordField getPasswordConfirmField() { return passwordConfirm; }

    public Span getErrorMessageField() { return errorMessageField; }

    public Button getSubmitButton() { return submitButton; }

    private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
        Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
    }

}
