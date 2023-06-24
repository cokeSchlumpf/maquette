package maquette.ui.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import maquette.infrastructure.UserContext;
import maquette.ui.layout.AbstractMaquetteAppView;
import maquette.ui.layout.MaquetteAppLayout;

@PageTitle("Maquette")
@Route(value = "", layout = MaquetteAppLayout.class)
public class Dashboard extends AbstractMaquetteAppView {

    public Dashboard(UserContext userContext) {
        super(userContext);

        this.add(new Span("Hello " + userContext.getUser().getDisplayName() + "!"));
    }

    @Override
    public UserContext getUserContext() {
        return userContext;
    }

}
