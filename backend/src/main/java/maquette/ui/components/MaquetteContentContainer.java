package maquette.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MaquetteContentContainer extends VerticalLayout {

    public MaquetteContentContainer() {
        this.setPadding(false);
        this.setMaxWidth(800, Unit.PIXELS);
    }

    public MaquetteContentContainer(Component... components) {
        this();
        this.add(components);
    }

}
