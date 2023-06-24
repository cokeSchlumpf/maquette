package maquette.ui.views.workspaces;

import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import maquette.infrastructure.UserContext;
import maquette.ui.components.RouterLinkWithIcon;
import maquette.ui.layout.MaquetteAppView;
import maquette.ui.views.WorkspacesView;

import java.util.List;

public abstract class AbstractWorkspaceView extends VerticalLayout implements MaquetteAppView, BeforeEnterObserver {

    protected String workspace;

    protected final UserContext userContext;

    public AbstractWorkspaceView(UserContext userContext) {
        this.userContext = userContext;
    }

    public void beforeEnter(BeforeEnterEvent event) {
        this.workspace = event
            .getRouteParameters()
            .get("name")
            .orElseThrow();
    }

    @Override
    public List<RouterLink> getMainMenuComponents() {
        var back = new RouterLinkWithIcon(
            VaadinIcon.ARROW_LEFT, "All Workspaces", WorkspacesView.class
        );
        back.setHighlightCondition(HighlightConditions.never());
        back.setDivider(true);

        var overview = new RouterLinkWithIcon(
            VaadinIcon.DASHBOARD, "Overview", WorkspaceView.class,
            new RouteParameters("name", workspace)
        );
        overview.setHighlightCondition(HighlightConditions.sameLocation());

        var sandboxes = new RouterLinkWithIcon(
            VaadinIcon.ABACUS, "Sandboxes", SandboxesView.class,
            new RouteParameters("name", workspace)
        );
        sandboxes.setDivider(true);

        return List.of(
            back,
            overview,
            new RouterLinkWithIcon(
                VaadinIcon.DATABASE, "Data Assets", DataAssetsView.class,
                new RouteParameters("name", workspace)
            ),
            new RouterLinkWithIcon(
                VaadinIcon.COMPILE, "Models", ModelsView.class,
                new RouteParameters("name", workspace)
            ),
            sandboxes,
            new RouterLinkWithIcon(
                VaadinIcon.COG, "Settings", SettingsView.class,
                new RouteParameters("name", workspace)
            )
        );
    }

    @Override
    public UserContext getUserContext() {
        return userContext;
    }
}
