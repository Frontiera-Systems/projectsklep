package com.example.application.security;

import com.example.application.service.ReCaptchaProperties;
import com.example.application.views.controllers.MainCustomLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Route(value = "login", layout = MainCustomLayout.class)
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();
    private final LoginI18n i18n = LoginI18n.createDefault();

    LoginI18n.Form i18nForm = i18n.getForm();
    private final String sitekey;
    private final String secretkey;
    private final LoginAttemptService loginAttemptService;
    private String redirectTarget = "";

    public LoginView(ReCaptchaProperties properties, LoginAttemptService loginAttemptService) {

        this.sitekey = properties.getSitekey();
        this.secretkey = properties.getSecretkey();
        this.loginAttemptService = loginAttemptService;

        ReCaptcha reCaptcha = new ReCaptcha(sitekey, secretkey);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.addForgotPasswordListener(event -> {
            Popover popover = new Popover();

            // Dodanie zawartości do Popover
            popover.add(new Span("Aby zrestartowac haslo, wyslij zdjecie stup administratorowi strony"));

            // Powiązanie Popover z przyciskiem
            popover.setTarget(login);

            // Otwieranie Popover
            popover.setOpened(true);
        });

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

        Html registerLink = new Html("<a href='reg' style='color: var(--lumo-primary-color); text-decoration: none;'>Nie masz konta? Zarejestruj się tutaj</a>");


        login.addLoginListener(event -> {
            String username = event.getUsername();
            String password = event.getPassword();
            String captchaToken = reCaptcha.getToken(); // Get the token from the reCAPTCHA component

            // Send the login request with the reCAPTCHA token
            UI.getCurrent().getPage().executeJs(
                    "const form = new FormData();" +
                            "form.append('username', $0);" +
                            "form.append('password', $1);" +
                            "form.append('g-recaptcha-response', $2);" +
                            "fetch('/login', {method: 'POST', body: form}).then(response => {" +
                            "    if (response.ok) { window.location.href = '/home'; } else { alert('Login failed.'); }" +
                            "});",
                    username, password, captchaToken
            );
        });

        login.setI18n(i18n);
        login.setAction("login");

        if(loginAttemptService.isBlocked()){
            removeAll();
            add(new H1("ZALOGUJ SIĘ"), login, reCaptcha, registerLink);
        } else {
            removeAll();
            add(new H1("ZALOGUJ SIĘ"), login, registerLink);
        }


    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }

        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .getOrDefault("redirect", List.of(""))
                .getFirst().equals("zamow")){
            Html orderLink = new Html("<a href='zamow' style='color: var(--lumo-primary-color); text-decoration: none;'>Kontynuuj jako gość</a>");
            add(new Text("lub"),orderLink);
        }

        if (getAuthenticatedUser()) {
            beforeEnterEvent.forwardTo("/"); // Przekierowanie na stronę główną
        }
    }


    public boolean getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return true;
        }
        // Anonymous or no authentication.
        return false;
    }


   /* @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        if (parameter != null && !parameter.isEmpty()) {
            redirectTarget = parameter;
        }
    }*/
}
