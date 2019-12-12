package maquette.controller.domain.api.commands.datasets;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.Command;
import maquette.controller.domain.api.validations.ObjectValidation;
import maquette.controller.domain.api.views.SimpleMessageVM;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserId;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateDatasetProducerToken implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String FOR = "for";

    @JsonProperty(PROJECT)
    private final String project;

    @JsonProperty(DATASET)
    private final String dataset;

    @JsonProperty(FOR)
    private final String forUser;

    @JsonCreator
    public static CreateDatasetProducerToken apply(
        @JsonProperty(PROJECT) String project,
        @JsonProperty(DATASET) String dataset,
        @JsonProperty(FOR) String forUser) {

        return new CreateDatasetProducerToken(project, dataset, forUser);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(dataset, DATASET);
        ResourcePath datasetResource = ResourcePath.apply(executor, project, dataset);

        UserId forUserId;

        if (forUser == null) {
            forUserId = executor.getUserId();
        } else {
            forUserId = UserId.apply(forUser);
        }

        return app
            .datasets()
            .createDatasetProducerToken(executor, forUserId, datasetResource)
            .thenApply(token -> {
                String sb = String.format(
                    "Created producer token '%s' with secret '%s'",
                    token.getDetails().getName(),
                    token.getSecret());

                return SimpleMessageVM.apply(sb);
            });
    }

}
