package maquette.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import maquette.core.domain.workspaces.Workspace;
import maquette.infrastructure.MaquetteSpringUserContext;
import maquette.ui.components.MaquetteGridWithControls;
import maquette.ui.layout.AbstractMaquetteAppView;
import maquette.ui.layout.MaquetteAppLayout;
import maquette.ui.views.workspace.WorkspaceView;

import java.util.List;
import java.util.stream.IntStream;

@PageTitle("Workspaces | Maquette")
@Route(value = "/workspaces", layout = MaquetteAppLayout.class)
public class WorkspacesView extends AbstractMaquetteAppView {

    public WorkspacesView(MaquetteSpringUserContext userContext) {
        super(userContext);

        this.add(
            new H3("Workspaces"),
            new Paragraph("Lorem ipsum dolor sit amet."),
            new WorkspacesGrid()
        );
    }

    private static class WorkspacesGrid extends MaquetteGridWithControls<Workspace> {

        public WorkspacesGrid() {
            var workspaceColumn = this
                .getGrid()
                .addComponentColumn(wks -> new RouterLink(wks.getName(), WorkspaceView.class, new RouteParameters(
                    "name", wks.getName().replaceAll(" ", "-").toLowerCase()
                )))
                .setHeader("Workspace")
                .setSortable(true);

            var descriptionColumn = this
                .getGrid()
                .addColumn(Workspace::getDescription)
                .setHeader("Description")
                .setSortable(false);

            var createdColumn = this
                .getGrid()
                .addColumn(wks -> wks.getCreated().getAt())
                .setHeader("Created")
                .setSortable(true);

            var modifiedColumn = this
                .getGrid()
                .addColumn(wks -> wks.getModified().getAt())
                .setHeader("Modified")
                .setSortable(true);

            this.getGrid().sort(List.of(
                new GridSortOrder<>(workspaceColumn, SortDirection.ASCENDING)
            ));

            var bttNew = this.getMenuBar().addItem("New Workspace");
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(CreateWorkspaceView.class));

            this.getGrid().setItems(
                IntStream
                    .range(0, 100)
                    .mapToObj(i -> Workspace.fake("Workspace " + i))
                    .toList()
            );

        }

    }

}
