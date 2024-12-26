package com.example.application.updateevents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class CartUpdatedEvent extends ComponentEvent<Component> {
    private final Long userId;

    public CartUpdatedEvent(Component source, Long userId) {
        super(source, false);
        this.userId = userId;
    }

}
