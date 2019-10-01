package maquette.controller.domain.api.namespaces;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

public interface NamespaceContainer {

    CompletionStage<DatasetDetails> createDataset(User executor, ResourcePath name, boolean isPrivate);

    CompletionStage<Done> deleteDataset(User executor, ResourcePath dataset);

    CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName name);

}
