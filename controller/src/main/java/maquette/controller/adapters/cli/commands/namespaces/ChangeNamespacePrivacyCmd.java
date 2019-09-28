package maquette.controller.adapters.cli.commands.namespaces;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeNamespacePrivacyCmd implements Command {

    private static final String NAMESPACE = "namespace";
    private static final String IS_PRIVATE = "is-private";

    @JsonProperty(NAMESPACE)
    private final String namespace;

    @JsonProperty(IS_PRIVATE)
    private final boolean isPrivate;

    @JsonCreator
    public static ChangeNamespacePrivacyCmd apply(
        @JsonProperty(NAMESPACE) String namespace,
        @JsonProperty(IS_PRIVATE) boolean isPrivate) {

        return new ChangeNamespacePrivacyCmd(namespace, isPrivate);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ResourceName resource = ResourceName.apply(executor, namespace);

        return app
            .namespaces()
            .changePrivacy(executor, resource, isPrivate)
            .thenApply(info -> CommandResult.success());
    }

}
