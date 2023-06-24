package maquette.ui.layout;

import com.vaadin.flow.router.RouterLink;
import maquette.infrastructure.UserContext;

import java.util.List;

public interface MaquetteAppView {

    List<RouterLink> getMainMenuComponents();

    UserContext getUserContext();

}
