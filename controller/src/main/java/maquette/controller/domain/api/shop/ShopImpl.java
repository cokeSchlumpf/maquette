package maquette.controller.domain.api.shop;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.services.CollectDatasets;
import maquette.controller.domain.services.CollectNamespaceInfos;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.deprecatedproject.ProjectDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectInfo;

@AllArgsConstructor(staticName = "apply")
public final class ShopImpl implements Shop {

    private final ActorRef<maquette.controller.domain.entities.project.protocol.ProjectsMessage> projectsRegistry;

    private final ActorRef<ProjectsMessage> namespacesRegistry;

    private final ActorRef<ShardingEnvelope<maquette.controller.domain.entities.project.protocol.ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> namespaces;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    @Override
    public CompletionStage<Set<DatasetDetails>> findDatasets(User executor, String query) {
        return listDatasets(executor)
            .thenApply(datasets -> datasets
                .stream()
                .filter(ds -> ds.getDescription().map(desc -> desc.getValue().contains(query)).orElse(false))
                .collect(Collectors.toSet()))
            .thenApply(datasets -> datasets
                .stream()
                .filter(ds -> ds.getAcl().canReadDetails(executor))
                .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> listDatasets(User executor) {
        CompletionStage<Set<ProjectInfo>> namespaceInfos = patterns
            .process(result -> CollectNamespaceInfos.create(namespacesRegistry, namespaces, result));

        CompletionStage<Set<DatasetDetails>> allDatasets = namespaceInfos
            .thenCompose(infos -> patterns.process(result -> CollectDatasets.create(infos, datasets, result)));

        return allDatasets.thenApply(datasets -> datasets
            .stream()
            .filter(ds -> !ds.getAcl().isPrivate() || ds.getAcl().canConsume(executor) || ds.getAcl().canProduce(executor))
            .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<Set<ProjectDetails>> listProjects(User executor) {
        return CompletableFuture.completedFuture(Sets.newHashSet());
    }

}
