package maquette.controller.domain.api.projects;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import com.google.common.collect.Sets;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.project.Project;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectDescription;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectOwner;
import maquette.controller.domain.entities.project.protocol.commands.ChangeProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.commands.CreateProject;
import maquette.controller.domain.entities.project.protocol.commands.DeleteProject;
import maquette.controller.domain.entities.project.protocol.commands.GrantProjectAccess;
import maquette.controller.domain.entities.project.protocol.commands.RevokeProjectAccess;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectDescription;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectOwner;
import maquette.controller.domain.entities.project.protocol.events.ChangedProjectPrivacy;
import maquette.controller.domain.entities.project.protocol.events.CreatedProject;
import maquette.controller.domain.entities.project.protocol.events.DeletedProject;
import maquette.controller.domain.entities.project.protocol.events.GrantedProjectAccess;
import maquette.controller.domain.entities.project.protocol.events.RevokedProjectAccess;
import maquette.controller.domain.entities.project.protocol.queries.GetProjectDetails;
import maquette.controller.domain.entities.project.protocol.results.GetProjectDetailsResult;
import maquette.controller.domain.services.CollectDatasets;
import maquette.controller.domain.util.ActorPatterns;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectInfo;
import maquette.controller.domain.values.project.ProjectPrivilege;

@AllArgsConstructor(staticName = "apply")
public final class ProjectsImpl implements Projects {

    private final ActorRef<ProjectsMessage> projectRegistry;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private CompletionStage<ProjectDetails> getProjectDetails(ResourceName project) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    GetProjectDetails.apply(project, replyTo, errorTo)),
                GetProjectDetailsResult.class)
            .thenApply(GetProjectDetailsResult::getDetails);
    }

    @Override
    public CompletionStage<ProjectDetails> changeDescription(User executor, ResourceName project, Markdown description) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    ChangeProjectDescription.apply(project, executor, description, replyTo, errorTo)),
                ChangedProjectDescription.class)
            .thenApply(ChangedProjectDescription::getProject)
            .thenCompose(this::getProjectDetails);
    }

    @Override
    public CompletionStage<ProjectDetails> changeOwner(User executor, ResourceName project, Authorization owner) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    ChangeProjectOwner.apply(project, executor, owner, replyTo, errorTo)),
                ChangedProjectOwner.class)
            .thenApply(ChangedProjectOwner::getProject)
            .thenCompose(this::getProjectDetails);
    }

    @Override
    public CompletionStage<ProjectDetails> changePrivacy(User executor, ResourceName project, boolean isPrivate) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    ChangeProjectPrivacy.apply(project, executor, isPrivate, replyTo, errorTo)),
                ChangedProjectPrivacy.class)
            .thenApply(ChangedProjectPrivacy::getProject)
            .thenCompose(this::getProjectDetails);
    }

    @Override
    public CompletionStage<ProjectDetails> create(User executor, ResourceName project, Markdown description, boolean isPrivate) {
        return patterns
            .ask(
                projectRegistry,
                (replyTo, errorTo) -> CreateProject.apply(project, executor, description, isPrivate, replyTo, errorTo),
                CreatedProject.class)
            .thenCompose(createdNamespace -> patterns.ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    CreateProject.apply(project, executor, description, isPrivate, replyTo, errorTo)),
                CreatedProject.class))
            .thenApply(CreatedProject::getName)
            .thenCompose(this::getProjectDetails);
    }

    @Override
    public CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName project) {
        return getProjectDetails(project)
            .thenCompose(details -> {
                ProjectInfo info =
                    ProjectInfo.apply(details.getName(), details.getModified(), details.getAcl(), details.getDatasets());
                Set<ProjectInfo> infos = Sets.newHashSet(info);

                return patterns.process(result -> CollectDatasets.create(infos, datasets, result));
            });
    }

    @Override
    public CompletionStage<Done> delete(User executor, ResourceName project) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    DeleteProject.apply(project, executor, replyTo, errorTo)),
                DeletedProject.class)
            .thenCompose(deleted -> patterns
                .ask(
                    projectRegistry,
                    (replyTo, errorTo) -> DeleteProject.apply(project, executor, replyTo, errorTo),
                    DeletedProject.class))
            .thenApply(deleted -> Done.getInstance());
    }

    @Override
    public CompletionStage<ProjectDetails> getDetails(User executor, ResourceName project) {
        return getProjectDetails(project);
    }

    @Override
    public CompletionStage<GrantedAuthorization> grantAccess(User executor, ResourceName project, ProjectPrivilege grant,
                                                             Authorization grantFor) {
        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    GrantProjectAccess.apply(project, executor, grant, grantFor, replyTo, errorTo)),
                GrantedProjectAccess.class)
            .thenApply(GrantedProjectAccess::getGrantedFor);
    }

    @Override
    public CompletionStage<GrantedAuthorization> revokeAccess(User executor, ResourceName project,
                                                              ProjectPrivilege revoke, Authorization revokeFrom) {

        return patterns
            .ask(
                projects,
                (replyTo, errorTo) -> ShardingEnvelope.apply(
                    Project.createEntityId(project),
                    RevokeProjectAccess.apply(project, executor, revoke, revokeFrom, replyTo, errorTo)),
                RevokedProjectAccess.class)
            .thenApply(RevokedProjectAccess::getRevokedFrom);
    }

}
