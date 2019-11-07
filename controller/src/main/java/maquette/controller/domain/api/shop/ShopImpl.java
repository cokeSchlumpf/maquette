package maquette.controller.domain.api.shop;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.services.CollectDatasets;
import maquette.controller.domain.services.CollectProjectDetails;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;

@AllArgsConstructor(staticName = "apply")
public final class ShopImpl implements Shop {

    private final ActorRef<ProjectsMessage> projectsRegistry;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private CompletionStage<Set<DatasetDetails>> getAllDatasets(User executor) {
        return patterns
            .<Set<ProjectDetails>>process(result -> CollectProjectDetails.create(projectsRegistry, this.projects, result))
            .thenApply(projects -> projects
                .stream()
                .filter(project -> project.getAcl().canFind(executor))
                .collect(Collectors.toSet()))
            .thenCompose(infos -> patterns.process(result -> CollectDatasets.create(infos, datasets, result)));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> findDatasets(User executor, String query) {
        CompletionStage<Set<DatasetDetails>> allDatasets = getAllDatasets(executor);

        return allDatasets.thenApply(datasets -> datasets
            .stream()
            .filter(ds -> ds.getAcl().canFind(executor))
            .filter(ds -> query == null || query.equals("*") || (ds.getDescription().isPresent() && ds
                .getDescription()
                .get()
                .getValue()
                .contains(query)) || ds
                              .getDataset()
                              .getName()
                              .getValue()
                              .contains(query))
            .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<Set<ProjectDetails>> findProjects(User executor, String query) {
        CompletionStage<Set<ProjectDetails>> namespaceInfos = patterns
            .process(result -> CollectProjectDetails.create(projectsRegistry, projects, result));

        return namespaceInfos.thenApply(projects -> projects
            .stream()
            .filter(proj -> proj.getAcl().canFind(executor))
            .filter(proj -> query == null || query.equals("*") || proj.getDescription().getValue().contains(query) || proj
                .getName()
                .getValue()
                .contains(query))
            .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> listDatasets(User executor) {
        CompletionStage<Set<DatasetDetails>> allDatasets = getAllDatasets(executor);

        return allDatasets.thenApply(datasets -> datasets
            .stream()
            .filter(ds -> ds.getAcl().canFind(executor))
            .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<Set<ProjectDetails>> listProjects(User executor) {
        CompletionStage<Set<ProjectDetails>> namespaceInfos = patterns
            .process(result -> CollectProjectDetails.create(projectsRegistry, projects, result));

        return namespaceInfos.thenApply(projects -> projects
            .stream()
            .filter(proj -> proj.getAcl().canFind(executor))
            .collect(Collectors.toSet()));
    }

}
