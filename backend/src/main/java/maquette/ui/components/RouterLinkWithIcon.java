package maquette.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class RouterLinkWithIcon extends RouterLink {

    public RouterLinkWithIcon(Icon icon, String text, Class<? extends Component> navigationTarget, RouteParameters paramters) {
        super(text, navigationTarget, paramters);

        this.removeAll();
        this.add(icon);
        this.add(new Text(text));

        this.setHighlightCondition(HighlightConditions.locationPrefix());
    }

    public RouterLinkWithIcon(Icon icon, String text, Class<? extends Component> navigationTarget) {
        this(icon, text, navigationTarget, RouteParameters.empty());
    }

    public RouterLinkWithIcon(VaadinIcon icon, String text, Class<? extends Component> navigationTarget, RouteParameters paramters) {
        this(new Icon(icon), text, navigationTarget, paramters);
    }

    public RouterLinkWithIcon(VaadinIcon icon, String text, Class<? extends Component> navigationTarget) {
        this(icon, text, navigationTarget, RouteParameters.empty());
    }

    public void setDivider(boolean value) {
        if (value) {
            this.addClassNames(LumoUtility.Margin.Bottom.LARGE);
        } else {
            this.removeClassName(LumoUtility.Margin.Bottom.LARGE);
        }
    }

}
