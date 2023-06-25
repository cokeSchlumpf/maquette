package maquette.ui.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import maquette.infrastructure.MaquetteSpringUserContext;
import maquette.ui.components.MaquetteRouterLinkWithIcon;
import maquette.ui.views.Dashboard;
import maquette.ui.views.WorkspacesView;

import java.util.List;

public class AbstractMaquetteAppView extends VerticalLayout implements MaquetteAppView {

    protected MaquetteSpringUserContext userContext;

    public AbstractMaquetteAppView(MaquetteSpringUserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public List<RouterLink> getMainMenuComponents() {
        return List.of(
            new MaquetteRouterLinkWithIcon(VaadinIcon.DASHBOARD, "Dashboard", Dashboard.class),
            new MaquetteRouterLinkWithIcon(VaadinIcon.WORKPLACE, "Workspaces", WorkspacesView.class)
        );
    }

    @Override
    public MaquetteSpringUserContext getUserContext() {
        return userContext;
    }

}
