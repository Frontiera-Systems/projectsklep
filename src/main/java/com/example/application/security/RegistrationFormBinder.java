package com.example.application.security;

import com.example.application.model.User;
import com.example.application.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationFormBinder {

    private final RegistrationForm registrationForm;
    private final UserService userService;
    /**
     * Flag for disabling first run for password validation
     */
    private boolean enablePasswordValidation;
    private boolean doUserExist = true;
    @Autowired
    public RegistrationFormBinder(RegistrationForm registrationForm, UserService userService) {
        this.registrationForm = registrationForm;
        this.userService = userService;
    }

    /**
     * Method to add the data binding and validation logics
     * to the registration form
     */

    public void addBindingAndValidation() {
        BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
        binder.bindInstanceFields(registrationForm);

        binder.forField(registrationForm.getPasswordField())
                .withValidator(this::passwordValidator)
                .bind("password");

        binder.forField(registrationForm.getUsernameField())
                .asRequired("Nazwa uzytkownika jest wymagana!")
                .bind("username");


        registrationForm.getPasswordConfirmField().addValueChangeListener(e -> {
            enablePasswordValidation = true;
            binder.validate();
        });

        // Set the label where bean-level error messages go
        binder.setStatusLabel(registrationForm.getErrorMessageField());

        // And finally the submit button
        registrationForm.getSubmitButton().addClickListener(event -> {
            try {
                User user = new User();
                binder.writeBean(user);

                // Save user via UserService
               doUserExist =  userService.registerUser(user,"DEFAULT");

                if(!doUserExist){
                    registrationForm.getUsernameField().setErrorMessage("Użytkownik o tej nazwie już istnieje!");
                    registrationForm.getUsernameField().setInvalid(true); // Mark the field as invalid

                } else{
                    UI.getCurrent().navigate("/login");
                }
            } catch (ValidationException e) {
                // Validation errors are already visible in the form
            }
        });
    }

    /**
     * Method to validate that:
     * <p>
     * 1) Password is at least 8 characters long
     * <p>
     * 2) Values in both fields match each other
     */
    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {
        if (pass1 == null || pass1.length() < 8) {
            return ValidationResult.error("Password should be at least 8 characters long");
        }

        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        String pass2 = registrationForm.getPasswordConfirmField().getValue();
        if (pass1.equals(pass2)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Passwords do not match");
    }

}

