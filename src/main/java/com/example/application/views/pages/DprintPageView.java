package com.example.application.views.pages;

import com.example.application.model.Item;
import com.example.application.repository.ItemRepository;
import com.example.application.views.controllers.StoreLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import java.util.List;

@Route(value = "/p3", layout = StoreLayout.class)
@PageTitle("P3")
//@RoutePrefix("mainmenu")
@AnonymousAllowed

public class DprintPageView extends Main implements HasComponents, HasStyle, RouterLayout {

    private OrderedList imageContainer;
    private final ItemRepository itemRepository;

    public DprintPageView(ItemRepository itemRepository) {
        constructUI();
        this.itemRepository = itemRepository;

        List<Item> items = itemRepository.findAll();

        for (Item product : items){
            imageContainer.add(new DprintPageViewCard(product));
        }

    }


    private void constructUI() {
        addClassNames("dprint-page-view");
        addClassNames(MaxWidth.SCREEN_XLARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Beautiful cars");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("kici kici");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sortuj");
        sortBy.setItems("Popularność", "Cena rosnąco", "Cena malejąco");
        sortBy.setValue("Popularność");

        imageContainer = new OrderedList();
        //imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        imageContainer.addClassNames(Gap.MEDIUM, Display.FLEX, FlexDirection.ROW, FlexWrap.WRAP, Margin.NONE, Padding.NONE, JustifyContent.CENTER);

        container.add(headerContainer, sortBy);
        setWidthFull();
        add(container,imageContainer);

    }
}
