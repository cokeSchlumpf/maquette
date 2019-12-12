package maquette.controller.domain.acl.shop;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import maquette.controller.domain.services.CreateDefaultProject;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;

@AllArgsConstructor(staticName = "apply")
public final class ShopUserActivity implements Shop {

    private final Shop delegate;

    private final CreateDefaultProject createDefaultProject;

    private <T> CompletionStage<T> createDefaultProject(User executor, Function<Shop, CompletionStage<T>> andThen) {
        return createDefaultProject.run(executor, () -> andThen.apply(delegate));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> findDatasets(User executor, String query) {
        return createDefaultProject(executor, p -> p.findDatasets(executor, query));
    }

    @Override
    public CompletionStage<Set<ProjectDetails>> findProjects(User executor, String query) {
        return createDefaultProject(executor, p -> p.findProjects(executor, query));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> listDatasets(User executor) {
        return createDefaultProject(executor, p -> p.listDatasets(executor));
    }

    @Override
    public CompletionStage<Set<ProjectDetails>> listProjects(User executor) {
        return createDefaultProject(executor, p -> p.listProjects(executor));
    }

}
