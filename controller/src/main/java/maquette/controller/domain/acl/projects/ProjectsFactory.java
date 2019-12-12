package maquette.controller.domain.acl.projects;

import akka.actor.typed.ActorRef;
import akka.cluster.sharding.typed.ShardingEnvelope;
import lombok.AllArgsConstructor;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.project.protocol.ProjectMessage;
import maquette.controller.domain.entities.project.protocol.ProjectsMessage;
import maquette.controller.domain.services.CreateDefaultProject;
import maquette.controller.domain.util.ActorPatterns;

@AllArgsConstructor(staticName = "apply")
public final class ProjectsFactory {

    private final ActorRef<ProjectsMessage> projectRegistry;

    private final ActorRef<ShardingEnvelope<ProjectMessage>> projects;

    private final ActorRef<ShardingEnvelope<DatasetMessage>> datasets;

    private final ActorPatterns patterns;

    private final CreateDefaultProject createDefaultProject;

    public Projects create() {
        Projects impl = ProjectsImpl.apply(projectRegistry, projects,datasets, patterns);
        Projects secured = ProjectsSecured.apply(projects, patterns, impl);
        return ProjectsUserActivity.apply(secured, createDefaultProject);
    }

}
