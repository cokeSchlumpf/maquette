package maquette.controller.domain.api.commands.commands.datasets;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.validations.ObjectValidation;
import maquette.controller.domain.api.commands.views.SimpleMessageVM;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApproveDatasetAccessRequestCmd implements Command {

    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String ID = "id";
    private static final String COMMENT = "comment";

    @JsonProperty(PROJECT)
    private final ResourceName project;

    @JsonProperty(DATASET)
    private final ResourceName dataset;

    @JsonProperty(ID)
    private final UID id;

    @JsonProperty(COMMENT)
    private final String comment;

    @JsonCreator
    public static ApproveDatasetAccessRequestCmd apply(
        @JsonProperty(PROJECT) ResourceName project,
        @JsonProperty(DATASET) ResourceName dataset,
        @JsonProperty(ID) UID id,
        @JsonProperty(COMMENT) String comment) {

        return new ApproveDatasetAccessRequestCmd(project, dataset, id, comment);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app,
                                          OutputFormat outputFormat) {
        ObjectValidation.notNull().validate(project, PROJECT);
        ObjectValidation.notNull().validate(project, DATASET);

        String com = Optional.ofNullable(comment).orElse("");
        
        return app
            .datasets()
            .approveAccessRequest(executor, ResourcePath.apply(project, dataset), id, Markdown.apply(com))
            .thenApply(info -> SimpleMessageVM.apply(info.getId().getValue()));
    }

}
