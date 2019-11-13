package maquette.controller.domain.entities.user.state;

import akka.persistence.typed.javadsl.Effect;
import maquette.controller.domain.entities.user.protocol.UserEvent;
import maquette.controller.domain.entities.user.protocol.commands.ConfigureNamespace;
import maquette.controller.domain.entities.user.protocol.commands.CreateDatasetAccessRequestLink;
import maquette.controller.domain.entities.user.protocol.commands.RegisterAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RemoveAccessToken;
import maquette.controller.domain.entities.user.protocol.commands.RenewAccessTokenSecret;
import maquette.controller.domain.entities.user.protocol.events.ConfiguredNamespace;
import maquette.controller.domain.entities.user.protocol.events.CreatedDatasetAccessRequestLink;
import maquette.controller.domain.entities.user.protocol.events.RegisteredAccessToken;
import maquette.controller.domain.entities.user.protocol.events.RemovedAccessToken;
import maquette.controller.domain.entities.user.protocol.queries.GetDetails;

public interface State {

    Effect<UserEvent, State> onConfigureNamespace(ConfigureNamespace configure);

    State onConfiguredNamespace(ConfiguredNamespace configured);

    Effect<UserEvent, State> onCreateDatasetAccessRequest(CreateDatasetAccessRequestLink create);

    State onCreatedDatasetAccessRequest(CreatedDatasetAccessRequestLink created);

    Effect<UserEvent, State> onGetDetails(GetDetails get);

    Effect<UserEvent, State> onRegisterAccessToken(RegisterAccessToken register);

    State onRegisteredAccessToken(RegisteredAccessToken registered);

    Effect<UserEvent, State> onRemoveAccessToken(RemoveAccessToken remove);

    State onRemovedAccessToken(RemovedAccessToken removed);

    Effect<UserEvent, State> onRenewAccessTokenSecret(RenewAccessTokenSecret renew);

}
