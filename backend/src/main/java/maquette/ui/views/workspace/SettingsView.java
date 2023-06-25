package maquette.ui.views.workspace;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import maquette.infrastructure.MaquetteSpringUserContext;
import maquette.ui.layout.MaquetteAppLayout;

@PageTitle("Workspace | Maquette")
@Route(value = "workspaces/:name/settings", layout = MaquetteAppLayout.class)
public class SettingsView extends AbstractWorkspaceView {

    public SettingsView(MaquetteSpringUserContext userContext) {
        super(userContext);

        this.add(
            new H3("Settings"),
            new Paragraph("Lorem ipsum dolor sit amet.")
        );
    }

}
