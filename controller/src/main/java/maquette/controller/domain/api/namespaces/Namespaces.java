package maquette.controller.domain.api.namespaces;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.namespace.NamespaceDetails;
import maquette.controller.domain.values.namespace.NamespaceInfo;
import maquette.controller.domain.values.namespace.NamespacePrivilege;

public interface Namespaces {

    CompletionStage<NamespaceInfo> changeDescription(User executor, ResourceName namespace, Markdown description);

    CompletionStage<NamespaceInfo> changeOwner(User executor,
                                               ResourceName namespaceName,
                                               Authorization owner);

    CompletionStage<NamespaceInfo> changePrivacy(User executor, ResourceName namespaceName, boolean isPrivate);

    CompletionStage<NamespaceInfo> createNamespace(User executor, ResourceName name, boolean isPrivate);

    CompletionStage<Done> deleteNamespace(User executor, ResourceName namespaceName);

    CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName namespace);

    CompletionStage<NamespaceDetails> getNamespaceDetails(User executor, ResourceName namespace);

    CompletionStage<GrantedAuthorization> grantNamespaceAccess(
        User executor, ResourceName namespaceName, NamespacePrivilege grant, Authorization grantFor);

    CompletionStage<Set<NamespaceInfo>> listNamespaces(User executor);

    CompletionStage<GrantedAuthorization> revokeNamespaceAccess(
        User executor, ResourceName namespaceName, NamespacePrivilege revoke, Authorization revokeFrom);

}
