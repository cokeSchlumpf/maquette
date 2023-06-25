package maquette.ui.views.workspace;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import maquette.infrastructure.MaquetteSpringUserContext;
import maquette.ui.components.MaquetteRouterLinkWithIcon;
import maquette.ui.layout.MaquetteAppView;
import maquette.ui.views.WorkspacesView;

import java.util.List;

public abstract class AbstractWorkspaceView extends VerticalLayout implements MaquetteAppView, BeforeEnterObserver {

    protected String workspace;

    protected final MaquetteSpringUserContext userContext;

    public AbstractWorkspaceView(MaquetteSpringUserContext userContext) {
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
        var back = new MaquetteRouterLinkWithIcon(
            VaadinIcon.ARROW_LEFT, "All Workspaces", WorkspacesView.class
        );
        back.setHighlightCondition(HighlightConditions.never());
        back.setDivider(true);

        var overview = new MaquetteRouterLinkWithIcon(
            VaadinIcon.DASHBOARD, "Overview", WorkspaceView.class,
            new RouteParameters("name", workspace)
        );
        overview.setHighlightCondition(HighlightConditions.sameLocation());

        var sandboxes = new MaquetteRouterLinkWithIcon(
            VaadinIcon.ABACUS, "Sandboxes", SandboxesView.class,
            new RouteParameters("name", workspace)
        );
        sandboxes.setDivider(true);

        return List.of(
            back,
            overview,
            new MaquetteRouterLinkWithIcon(
                VaadinIcon.DATABASE, "Data Assets", DataAssetsView.class,
                new RouteParameters("name", workspace)
            ),
            new MaquetteRouterLinkWithIcon(
                VaadinIcon.COMPILE, "Models", ModelsView.class,
                new RouteParameters("name", workspace)
            ),
            sandboxes,
            new MaquetteRouterLinkWithIcon(
                VaadinIcon.COG, "Settings", SettingsView.class,
                new RouteParameters("name", workspace)
            )
        );
    }

    @Override
    public MaquetteSpringUserContext getUserContext() {
        return userContext;
    }
}
