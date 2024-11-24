package com.example.application.views.mainview;

import com.example.application.views.mainview.mainpage.DprintPageViewCard;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "", layout = MainLayout.class)
@PageTitle("STRONA GŁOWNA")

public class MainViewView extends VerticalLayout {

    private OrderedList imageContainer;

    public MainViewView() {

        HorizontalLayout rootHorizontal = new HorizontalLayout();
        imageContainer = new OrderedList();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);

        rootHorizontal.add(imageContainer);

        imageContainer.add(new DprintPageViewCard("Snow mountains under stars",
                "https://images.unsplash.com/photo-1519681393784-d120267933ba?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80"));

    }

}
