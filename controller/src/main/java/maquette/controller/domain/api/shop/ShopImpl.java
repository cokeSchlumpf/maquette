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
import maquette.controller.domain.entities.namespace.protocol.NamespaceMessage;
import maquette.controller.domain.entities.namespace.protocol.NamespacesMessage;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.services.CollectDatasets;
import maquette.controller.domain.services.CollectNamespaceInfos;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.controller.domain.values.project.ProjectDetails;

@AllArgsConstructor(staticName = "apply")
public final class ShopImpl implements Shop {

    private final ActorRef<ProjectsMessage> projectsRegistry;

    private final ActorRef<NamespacesMessage> namespacesRegistry;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<NamespaceMessage>> namespaces;

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
                .filter(ds -> ds.getAcl().canView(executor))
                .collect(Collectors.toSet()));
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> listDatasets(User executor) {
        CompletionStage<Set<NamespaceInfo>> namespaceInfos = patterns
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
