package com.example.application.views.pages.categories;

import com.example.application.model.Category;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class CategoriesViewCard extends ListItem {

    public CategoriesViewCard(Category category){
        setWidth("20%");
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.START, LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(LumoUtility.Background.CONTRAST, LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Overflow.HIDDEN, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Width.FULL);

        Image image = new Image(category.getImageUrl(), "");
        image.setWidth("100%");
        div.add(image);

        Span header = new Span( new Anchor("/" + category.getFullPath(), category.getName()));
        header.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD);

        Div headerContainer = new Div(header);
        headerContainer.setHeight("5%");
        headerContainer.setWidth("100%");
        headerContainer.addClassNames(LumoUtility.Display.FLEX, LumoUtility.JustifyContent.CENTER);

        add(div, headerContainer);
    }
}
