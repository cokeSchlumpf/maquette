package maquette.ui.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import maquette.core.application.commands.WorkspacesCreateCommand;
import maquette.infrastructure.MaquetteSpringCommandRunner;
import maquette.infrastructure.MaquetteSpringUserContext;
import maquette.ui.components.MaquetteCommandFormBuilder;
import maquette.ui.components.MaquetteContentContainer;
import maquette.ui.layout.AbstractMaquetteAppView;
import maquette.ui.layout.MaquetteAppLayout;

@PageTitle("Create new workspace | Maquette")
@Route(value = "/workspaces/_create", layout = MaquetteAppLayout.class)
public class CreateWorkspaceView extends AbstractMaquetteAppView {

    public CreateWorkspaceView(MaquetteSpringUserContext userContext, MaquetteSpringCommandRunner commandRunner) {
        super(userContext);

        var form = new MaquetteCommandFormBuilder<>(
            WorkspacesCreateCommand.class,
            commandRunner,
            () -> WorkspacesCreateCommand.apply("", "")
        ).addVariant(
            "name",
            MaquetteCommandFormBuilder.FormVariant.FULL_WIDTH
        ).addVariant(
            "description",
            MaquetteCommandFormBuilder.FormVariant.FULL_WIDTH
        ).build();

        this.add(
            new H3(
                new RouterLink("Workspaces", WorkspacesView.class),
                new Text(" » Create new workspace")
            ),

            new MaquetteContentContainer(
                new Paragraph("Create new workspace."),
                form
            )
        );
    }

}
