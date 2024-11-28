package com.example.application.security;

import com.example.application.service.UserService;
import com.example.application.views.controllers.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@AnonymousAllowed
@Route(value = "reg", layout = MainLayout.class)
public class RegistrationView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Sprawdzenie, czy użytkownik jest zalogowany
        if (getAuthenticatedUser()) {
            event.forwardTo("/"); // Przekierowanie na stronę główną
        }
    }
    public RegistrationView(UserService userService, AuthenticationContext authContext) {

        RegistrationForm registrationForm = new RegistrationForm();

        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);

        add(registrationForm);

        RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(registrationForm, userService);
        registrationFormBinder.addBindingAndValidation();
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

}
