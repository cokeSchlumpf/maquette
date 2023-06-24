package maquette.ui.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;

public class MaquetteGrid<T> extends Grid<T> {

    public MaquetteGrid() {
        this.setAllRowsVisible(true);

        this.addThemeVariants(
            GridVariant.LUMO_ROW_STRIPES
        );
    }

}
