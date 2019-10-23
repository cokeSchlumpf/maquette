package maquette.controller.adapters.cli.commands.datasets;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.adapters.cli.commands.EAuthorizationType;
import maquette.controller.adapters.cli.validations.ObjectValidation;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.governance.GovernanceProperties;
import maquette.controller.domain.values.dataset.DatasetPrivilege;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestDatasetAccessCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String JUSTIFICATION = "justification";
    private static final String AUTHORIZATION = "authorization";
    private static final String PRIVILEGE = "privilege";
    private static final String TO = "to";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonProperty(JUSTIFICATION)
    private final String justification;

    @JsonProperty(AUTHORIZATION)
    private final EAuthorizationType authorization;

    @JsonProperty(PRIVILEGE)
    private final DatasetPrivilege privilege;

    @JsonProperty(TO)
    private final String to;

    @JsonCreator
    public static RequestDatasetAccessCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DATASET) ResourceName dataset,
        @JsonProperty(JUSTIFICATION) String justification,
        @JsonProperty(AUTHORIZATION) EAuthorizationType authorization,
        @JsonProperty(PRIVILEGE) DatasetPrivilege privilege,
        @JsonProperty(TO) String to) {

        return new RequestDatasetAccessCmd(project, dataset, justification, authorization, privilege, to);
    }

    @Override
    public CompletionStage<CommandResult> run(User executor, CoreApplication app) {
        ObjectValidation.notNull().validate(project, PROJECT);
        ObjectValidation.notNull().validate(project, DATASET);
        ObjectValidation.notNull().validate(to, TO);
        ObjectValidation
            .validAuthorization(to)
            .and(ObjectValidation.notNull())
            .validate(authorization, AUTHORIZATION);
        
        return app
            .datasets()
            .requestDatasetAccess(executor, ResourcePath.apply(project, dataset), justification, privilege, authorization.asAuthorization(to))
            .thenApply(request -> {
                if (request.getApproved().isPresent()) {
                    return CommandResult.success("APPROVED " + request.getId().getValue());
                } else {
                    return CommandResult.success("REQUESTED " + request.getId().getValue());
                }
            });
    }

}
