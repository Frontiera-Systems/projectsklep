package com.example.application.views.mainview;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("STRONA GŁOWNA")

public class MainViewView extends VerticalLayout {

    public MainViewView() {
        Anchor frontpage = new Anchor();
        frontpage.setHref("/podstrona");
        frontpage.setText("Frontpage");
        add(frontpage);
        Anchor frontpage2 = new Anchor();
        frontpage2.setHref("/podstrona/podstrona2");
        frontpage2.setText("Frontpage2");
        add(frontpage2);
}

}
