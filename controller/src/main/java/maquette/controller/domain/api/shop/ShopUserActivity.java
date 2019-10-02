package maquette.controller.domain.api.shop;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import maquette.controller.domain.services.CreateDefaultNamespace;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;

@AllArgsConstructor(staticName = "apply")
public final class ShopUserActivity implements Shop {

    private final Shop delegate;

    private final CreateDefaultNamespace createDefaultNamespace;

    private <T> CompletionStage<T> createDefaultNamespace(User executor, Function<Shop, CompletionStage<T>> andThen) {
        return createDefaultNamespace.run(executor, () -> andThen.apply(delegate));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> findDatasets(User executor, String query) {
        return createDefaultNamespace(executor, p -> p.findDatasets(executor, query));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> listDatasets(User executor) {
        return createDefaultNamespace(executor, p -> p.listDatasets(executor));
    }

    @Override
    public CompletionStage<Set<ProjectDetails>> listProjects(User executor) {
        return createDefaultNamespace(executor, p -> p.listProjects(executor));
    }

}
