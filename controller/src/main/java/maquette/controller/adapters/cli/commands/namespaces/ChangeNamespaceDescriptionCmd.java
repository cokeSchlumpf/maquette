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
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeNamespaceDescriptionCmd implements Command {

    private static final String NAMESPACE = "namespace";
    private static final String DESCRIPTION = "description";

    @JsonProperty(NAMESPACE)
    private final String namespace;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonCreator
    public static ChangeNamespaceDescriptionCmd apply(
        @JsonProperty(NAMESPACE) String namespace,
        @JsonProperty(DESCRIPTION) Markdown description) {

        return new ChangeNamespaceDescriptionCmd(namespace, description);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ResourceName resource = ResourceName.apply(executor, namespace);

        return app
            .namespaces()
            .changeDescription(executor, resource, description)
            .thenApply(info -> CommandResult.success());
    }

}
