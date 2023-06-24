package maquette.ui.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import maquette.infrastructure.UserContext;
import maquette.ui.components.RouterLinkWithIcon;
import maquette.ui.views.Dashboard;
import maquette.ui.views.WorkspacesView;

import java.util.List;

public class AbstractMaquetteAppView extends VerticalLayout implements MaquetteAppView {

    protected UserContext userContext;

    public AbstractMaquetteAppView(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public List<RouterLink> getMainMenuComponents() {
        return List.of(
            new RouterLinkWithIcon(VaadinIcon.DASHBOARD, "Dashboard", Dashboard.class),
            new RouterLinkWithIcon(VaadinIcon.WORKPLACE, "Workspaces", WorkspacesView.class)
        );
    }

    @Override
    public UserContext getUserContext() {
        return userContext;
    }

}
