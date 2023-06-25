package maquette.ui.views.workspace;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import maquette.infrastructure.MaquetteSpringUserContext;
import maquette.ui.layout.MaquetteAppLayout;

@PageTitle("Workspace | Maquette")
@Route(value = "workspaces/:name/models", layout = MaquetteAppLayout.class)
public class ModelsView extends AbstractWorkspaceView {

    public ModelsView(MaquetteSpringUserContext userContext) {
        super(userContext);

        this.add(
            new H3("Models"),
            new Paragraph("Lorem ipsum dolor sit amet.")
        );
    }

}
