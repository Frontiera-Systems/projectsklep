package com.example.application.views.mainview.security;

import com.example.application.views.mainview.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", layout = MainLayout.class)
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private LoginForm login = new LoginForm();
    private LoginI18n i18n = LoginI18n.createDefault();
    private LoginI18n.Header i18nHeader = new LoginI18n.Header();
    LoginI18n.Form i18nForm = i18n.getForm();

    public LoginView() {
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        i18nForm.setTitle("");
        i18nForm.setUsername("Email");
        i18nForm.setPassword("Hasło");
        i18nForm.setSubmit("ZALOGUJ SIE");
        i18nForm.setForgotPassword("Zapomniales hasla?");
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setPassword("Hasło jest wymagane");
        i18nErrorMessage.setUsername("Email jest wymagany");
        i18nErrorMessage.setTitle("Nieprawidłowe dane logowania");
        i18nErrorMessage.setMessage("Sprawdź czy email oraz hasło są poprawne natępnie spróbuj ponownie.");
        i18n.setErrorMessage(i18nErrorMessage);

        Html registerLink = new Html("<a href='p3' style='color: var(--lumo-primary-color); text-decoration: none;'>Nie masz konta? Zarejestruj się tutaj</a>");

        login.setAction("login");
        login.setI18n(i18n);

        add(new H1("ZALOGUJ SIĘ"),login,registerLink);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
