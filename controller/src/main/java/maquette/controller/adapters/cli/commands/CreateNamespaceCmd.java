package maquette.controller.adapters.cli.commands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateNamespaceCmd implements Command {

    private final String name;

    @JsonCreator
    public static CreateNamespaceCmd apply(@JsonProperty("name") String name) {
        return new CreateNamespaceCmd(name);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ResourceName resource;

        if (name == null) {
            resource = ResourceName.apply(executor.getUserId().getId());
        } else {
            resource = ResourceName.apply(name);
        }

        return app
            .namespaces()
            .createNamespace(executor, resource)
            .thenApply(info -> CommandResult.success());
    }

}
