package maquette.controller.domain.api.commands.shop;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.OutputFormat;
import maquette.controller.domain.api.ViewModel;
import maquette.controller.domain.api.Command;
import maquette.controller.domain.api.views.ProjectsVM;
import maquette.controller.domain.values.iam.User;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FindProjectsCmd implements Command {

    private static final String QUERY = "query";

    @JsonProperty(QUERY)
    private final String query;

    @JsonCreator
    public static FindProjectsCmd apply(@JsonProperty(QUERY) String query) {
        return new FindProjectsCmd(query);
    }

    public static FindProjectsCmd apply() {
        return new FindProjectsCmd(null);
    }

    @Override
    public CompletionStage<ViewModel> run(User executor, CoreApplication app, OutputFormat outputFormat) {
        return app
            .shop()
            .findProjects(executor, query)
            .thenApply(projects -> ProjectsVM.apply(projects, executor, outputFormat));
    }

}
