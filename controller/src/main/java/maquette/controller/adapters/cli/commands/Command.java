package maquette.controller.adapters.cli.commands;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.iam.User;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "command")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = CreateNamespaceCmd.class, name = "namespaces create"),
        @JsonSubTypes.Type(value = ListNamespacesCmd.class, name = "namespaces")
    })
public interface Command {

    CompletionStage<CommandResult> run(User executor, CoreApplication app);

}
