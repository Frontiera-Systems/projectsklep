package com.example.application.views.mainview;

import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("STRONA GŁOWNA")

public class MainViewView extends VerticalLayout {

    private OrderedList imageContainer;

    public MainViewView() {

    }



}
