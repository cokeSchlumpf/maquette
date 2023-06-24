package maquette.ui.views.workspaces;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import maquette.infrastructure.UserContext;
import maquette.ui.layout.MaquetteAppLayout;

@PageTitle("Workspace | Maquette")
@Route(value = "workspaces/:name", layout = MaquetteAppLayout.class)
public class WorkspaceView extends AbstractWorkspaceView {

    public WorkspaceView(UserContext userContext) {
        super(userContext);

        this.add(
            new H3("Workspace"),
            new Paragraph("Lorem ipsum dolor sit amet.")
        );
    }

}
