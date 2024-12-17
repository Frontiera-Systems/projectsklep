package com.example.application.views.pages.items;

import com.example.application.model.Item;
import com.example.application.service.CartService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ItemView extends Main implements HasComponents, HasStyle {

    private int ITEMS_PER_PAGE = 24;
    private int currentPage = 1;
    private OrderedList itemsContainer;
    private List<Item> items;
    private Button nextButton;
    private Button prevButton;
    private CartService cartService;

    public ItemView(List<Item> items) {

        this.items = items;
        itemsContainer = new OrderedList();

        itemsContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.JustifyContent.CENTER);


        int totalPages = getTotalPages();
        updateItems();

        addClassNames("dprint-page-view");
        addClassNames(LumoUtility.MaxWidth.SCREEN_XLARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        HorizontalLayout buttonContainer = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.END);

        itemsContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.JustifyContent.CENTER);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sortuj");
        sortBy.setItems("Nazwa A-Z", "Nazwa Z-A", "Cena rosnąco", "Cena malejąco");
        sortBy.setValue("Nazwa A-Z");

        sortBy.addValueChangeListener(event -> {

            if (Objects.equals(event.getValue(), "Cena rosnąco")) {
                items.sort(Comparator.comparing(Item::getPrice));
            }

            if (Objects.equals(event.getValue(), "Cena malejąco")) {
                items.sort(Comparator.comparing(Item::getPrice).reversed());
            }

            if (Objects.equals(event.getValue(), "Nazwa A-Z")) {
                items.sort(Comparator.comparing(Item::getName, String::compareToIgnoreCase));
            }

            if (Objects.equals(event.getValue(), "Nazwa Z-A")) {
                items.sort(Comparator.comparing(Item::getName, String::compareToIgnoreCase).reversed());
            }
            updateItems();
        });

        getTotalPages();

        Button currentPageButton = new Button("Strona " + currentPage + " z " + totalPages);
        currentPageButton.setEnabled(false);


        prevButton = new Button("Poprzednia",
                new Icon(VaadinIcon.ARROW_LEFT), event -> {
            nextButton.setEnabled(true);
            if (currentPage > 1) {
                currentPage--;
                updateItems();
                prevButton.setEnabled(true);
            }
            currentPageButton.setText("Strona " + currentPage + " z " + totalPages);
        });

        prevButton.setDisableOnClick(true);
        prevButton.setEnabled(false);

        nextButton = new Button("Następna",
                new Icon(VaadinIcon.ARROW_RIGHT),
                 event -> {
                prevButton.setEnabled(true);
                nextButton.setEnabled(false);
            if (currentPage < getTotalPages()) {
                currentPage++;
                updateItems();
                nextButton.setEnabled(true);
            }

            currentPageButton.setText("Strona " + currentPage + " z " + totalPages);

        });

        nextButton.setIconAfterText(true);
        nextButton.setDisableOnClick(true);


        buttonContainer.add(prevButton, currentPageButton, nextButton);
        container.add(sortBy,buttonContainer);
        setWidthFull();
        add(container, itemsContainer);

    }

    private int getTotalPages() {
        return (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);
    }

    private void updateItems() {
        itemsContainer.removeAll();
        // Oblicz zakres elementów dla aktualnej strony

        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, items.size());

        List<Item> pageItems = items.subList(start, end);
        // Dodaj elementy na aktualną stronę
        pageItems.forEach(item -> itemsContainer.add(new ItemsViewCard(item)));
    }


}
