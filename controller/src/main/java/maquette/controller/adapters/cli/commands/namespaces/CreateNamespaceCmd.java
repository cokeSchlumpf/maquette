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
public class CreateNamespaceCmd implements Command {

    private final String namespace;

    @JsonCreator
    public static CreateNamespaceCmd apply(@JsonProperty("namespace") String namespace) {
        return new CreateNamespaceCmd(namespace);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ResourceName resource = ResourceName.apply(executor, namespace);

        return app
            .namespaces()
            .createNamespace(executor, resource)
            .thenApply(info -> CommandResult.success());
    }

}
