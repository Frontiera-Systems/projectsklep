package com.example.application.views.pages.user;

import com.example.application.model.Cart;
import com.example.application.model.CartItem;
import com.example.application.model.Item;
import com.example.application.model.User;
import com.example.application.repository.CartItemRepository;
import com.example.application.repository.CartRepository;
import com.example.application.repository.ItemRepository;
import com.example.application.repository.UserRepository;
import com.example.application.security.SecurityService;
import com.example.application.service.CartService;
import com.example.application.service.OrderService;
import com.example.application.service.SessionCartService;
import com.example.application.views.controllers.MainCustomLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.*;

@Route(value = "koszyk", layout = MainCustomLayout.class)
@AnonymousAllowed
public class CartView extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final CartService cartService;
    private final SessionCartService sessionCartService;
    private final OrderService orderService;
    private Map<CartItem, Integer> temporaryQuantities = new HashMap<>();
    private VerticalLayout listDiv;
    private Long userId;
    MultiSelectListBox<Integer> sessionItems;
    MultiSelectListBox<CartItem> itemList;
    public CartView(ItemRepository itemRepository, CartItemRepository cartItemRepository, CartRepository cartRepository, UserRepository userRepository, SecurityService securityService, CartService cartService, SessionCartService sessionCartService, OrderService orderService) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.cartService = cartService;
        this.sessionCartService = sessionCartService;
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.orderService = orderService;

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        listDiv = new VerticalLayout();
        listDiv.setJustifyContentMode(JustifyContentMode.CENTER);
        listDiv.setAlignItems(Alignment.CENTER);
        userId = securityService.getAuthenticatedUserId();
        if(userId.equals(0L)){
            constructSessionUI();
        } else {
            User user = userRepository.findById(userId);
            Cart cart = cartRepository.findByUserId(user.getId());
            constructUI(cart);
            }
        add(listDiv);
    }


    private void constructUI(Cart cart) {
        /*removeAll();
        List<CartItem> itemsInCart = cartItemRepository.findByCartId(cart.getId());
        itemList = cartService.multiitembox(itemsInCart, temporaryQuantities, cart);
        if(itemsInCart.isEmpty()){
            thecartEmpty();
        } else {
            Button order = orderButton();
            listDiv.add(itemList, order);
        }*/

        removeAll();


        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        Grid<CartItem> cartGrid = new Grid<>(CartItem.class, false);

        Div gridContainer = new Div();
        gridContainer.addClassName("my-grid-container");
        gridContainer.add(cartGrid);

        cartGrid.setAllRowsVisible(true);
        cartGrid.addClassName("cart-grid");
        cartGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        cartGrid.addColumn(createCartItemRenderer())
                .setHeader("Produkt").setAutoWidth(true).setFlexGrow(1).setHeaderPartName("header");

        cartGrid.addComponentColumn(cartItem -> {
            IntegerField quantityfield = new IntegerField();
            quantityfield.setMax(cartItem.getItem().getQuantity());
            quantityfield.setMin(0);
            quantityfield.setValue(cartItem.getQuantity());
            quantityfield.addValueChangeListener(value -> {
                cartItem.setQuantity(value.getValue());
                cartItemRepository.save(cartItem);
                cartGrid.setItems(cartService.getItemsInCart(cart.getId()));
            });
            quantityfield.setWidth("75%");
            quantityfield.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
            return quantityfield;
        }).setHeader("Ilosc").setHeaderPartName("header");;

        cartGrid.addColumn(cartItem -> cartItem.getItem().getPrice()*cartItem.getQuantity() + " zł")
                .setHeader("Razem").setAutoWidth(true).setFlexGrow(1).setHeaderPartName("header");;

        cartGrid.addComponentColumn(cartItem -> {
            Button removeButton = new Button(new Icon(VaadinIcon.TRASH), click -> {
                cartService.removeCartItem(cartItem.getId());
                cartGrid.setItems(cartService.getItemsInCart(cart.getId()));
            });
            removeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return removeButton;
        }).setAutoWidth(true).setFlexGrow(0).setHeaderPartName("header");;

        cartGrid.setItems(cartItems);

        if(cartItems.isEmpty()){
            thecartEmpty();
        } else {
            Button order = orderButton();
            add(gridContainer, order);
        }
    }

    private static Renderer<CartItem> createCartItemRenderer() {
        return LitRenderer.<CartItem>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <img src=\"${item.pictureUrl}\" alt=\"Item image\" style=\"width: 50px; height: 50px; border-radius: 50%; margin-right: var(--lumo-space-m);\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.fullName}"
                                + "    </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.id}"
                                + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>"
                )
                .withProperty("pictureUrl", cartItem -> cartItem.getItem().getImageUrl()) // URL do obrazu
                .withProperty("fullName", cartItem -> cartItem.getItem().getName())      // Nazwa przedmiotu
                .withProperty("id", cartItem -> "Indeks: " + cartItem.getItem().getId()); // Indeks z dodanym napisem
    }


    private void constructSessionUI(){
        removeAll();
        Map<Integer, Integer> sessionCart = sessionCartService.getCart();

        Grid<Integer> cartGrid = new Grid<>();

        Div gridContainer = new Div();

        gridContainer.addClassName("my-grid-container");
        gridContainer.add(cartGrid);

        cartGrid.setAllRowsVisible(true);
        cartGrid.addClassName("cart-grid");
        cartGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        cartGrid.addColumn(createCartItemSessionRenderer(sessionCartService))
                .setHeader("Produkt").setAutoWidth(true).setFlexGrow(1).setHeaderPartName("header");

        cartGrid.addComponentColumn(cartItem -> {
            Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
            IntegerField quantityfield = new IntegerField();
            quantityfield.setMax(optionalItem.get().getQuantity());
            quantityfield.setMin(0);
            quantityfield.setValue(sessionCart.get(cartItem));
            quantityfield.addValueChangeListener(value -> {
                Integer newQuantity = value.getValue();
                sessionCartService.addToCart(cartItem, newQuantity - sessionCart.get(cartItem));
                cartGrid.setItems(sessionCart.keySet());
            });
            quantityfield.setWidth("75%");
            quantityfield.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
            return quantityfield;
        }).setHeader("Ilosc").setHeaderPartName("header");

        cartGrid.addComponentColumn(cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    String formattedPrice = String.format("%.2f", optionalItem.get().getPrice() * sessionCart.get(cartItem));
                    return new Text(formattedPrice + " zł");
                })
                .setHeader("Razem").setAutoWidth(true).setFlexGrow(1).setHeaderPartName("header");;

        cartGrid.addComponentColumn(cartItem -> {
            Button removeButton = new Button(new Icon(VaadinIcon.TRASH), click -> {
                sessionCartService.removeFromCart(cartItem);
                cartGrid.setItems(sessionCart.keySet());
            });
            removeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return removeButton;
        }).setAutoWidth(true).setFlexGrow(0).setHeaderPartName("header");;

        cartGrid.setItems(sessionCart.keySet());

        if(sessionCart.keySet().isEmpty()){
            thecartEmpty();
        } else {
            Button order = orderButton();
            add(gridContainer, order);
        }
    }

    private static Renderer<Integer> createCartItemSessionRenderer(SessionCartService sessionCartService) {
        return LitRenderer.<Integer>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <img src=\"${item.pictureUrl}\" alt=\"Item image\" style=\"width: 50px; height: 50px; border-radius: 50%; margin-right: var(--lumo-space-m);\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;\">"
                                + "      ${item.fullName}"
                                + "    </span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      ${item.id}"
                                + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>"
                )
                .withProperty("pictureUrl", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    return optionalItem.get().getImageUrl();
                }) // URL do obrazu
                .withProperty("fullName", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    return optionalItem.get().getName();
                })      // Nazwa przedmiotu
                .withProperty("id", cartItem -> {
                    Optional<Item> optionalItem = sessionCartService.itemRepository.findById(cartItem);
                    return "Indeks: " + optionalItem.get().getId();
                }); // Indeks z dodanym napisem
    }

    private void thecartEmpty(){
        Text cartEmpty = new Text("Twoj koszyk jest pusty :( \n Przejdz na stronę główną i zapełnij go czymś!");
        Button redirectToMain = new Button("POWRÓT DO STRONY GŁOWNEJ");
        redirectToMain.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ERROR);
        redirectToMain.addClickListener(click -> {
            UI.getCurrent().navigate("");
        });
        listDiv.add(cartEmpty,redirectToMain);
        listDiv.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER);
    }


    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {

    }

    private Button orderButton(){
        Button orderButton = new Button("REALIZUJ ZAMOWIENIE");
        orderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SUCCESS);
        orderButton.addClickListener(click -> {
            if(userId.equals(0L)){
                Set<Integer> selectedItems = sessionItems.getSelectedItems();
                UI.getCurrent().getSession().setAttribute("selectedItems",selectedItems);
                UI.getCurrent().getPage().setLocation("/login?redirect=zamow");;
            } else {
                Set<CartItem> selectedItems = itemList.getSelectedItems();
                UI.getCurrent().getSession().setAttribute("selectedItems", selectedItems);
                UI.getCurrent().navigate("zamow");
            }
        });
        return orderButton;
    }

    private void ui(){

    }
}
