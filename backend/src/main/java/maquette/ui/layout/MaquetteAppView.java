package maquette.ui.layout;

import com.vaadin.flow.router.RouterLink;
import maquette.infrastructure.MaquetteSpringUserContext;

import java.util.List;

public interface MaquetteAppView {

    List<RouterLink> getMainMenuComponents();

    MaquetteSpringUserContext getUserContext();

}
