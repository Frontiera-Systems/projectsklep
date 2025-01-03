package com.example.application.updateevents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

public class ItemViewUpdatedEvent extends ComponentEvent<Component> {
    @Getter
    private final boolean status;
    @Getter
    private final int button; // Trzeci argument

    public ItemViewUpdatedEvent(Component source, boolean status, int button) {
        super(source, false);
        this.status = status;
        this.button = button;
    }


}
