package com.example.application.views.mainview;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "podstrona", layout = MainLayout.class)
@PageTitle("PODSTRONA")
@RolesAllowed("ADMIN")
public class PodstronaPierwsza extends VerticalLayout {

    public PodstronaPierwsza() {

}

}
