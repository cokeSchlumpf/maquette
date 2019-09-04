package maquette.controller.domain.entities.dataset.services;

import java.time.Duration;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.DatasetMessage;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.commands.CommitDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishCommittedDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.commands.PublishDatasetVersion;
import maquette.controller.domain.entities.dataset.protocol.events.CommittedDatasetVersion;
import maquette.controller.domain.values.core.ErrorMessage;

public class PublishVersion {

    public static Behavior<Message> create(
        ActorRef<DatasetMessage> dataset, ActorRef<VersionMessage> version, PublishDatasetVersion request) {

        return Behaviors.setup(ctx -> {
            final ActorRef<CommittedDatasetVersion> committedAdapter =
                ctx.messageAdapter(CommittedDatasetVersion.class, CommittedDatasetVersionWrapper::new);

            final ActorRef<ErrorMessage> errorAdapter =
                ctx.messageAdapter(ErrorMessage.class, ErrorMessageWrapper::new);

            final CommitDatasetVersion commit = CommitDatasetVersion.apply(
                request.getExecutor(),
                request.getDataset(),
                request.getVersionId(),
                request.getMessage(),
                committedAdapter,
                errorAdapter);

            return Behaviors.withTimers(timer -> {
                version.tell(commit);
                timer.startSingleTimer(Timeout.class, new Timeout(), Duration.ofSeconds(10));

                return Behaviors
                    .receive(Message.class)
                    .onMessage(CommittedDatasetVersionWrapper.class, (actor, wrapper) -> {
                        PublishCommittedDatasetVersion publish = PublishCommittedDatasetVersion.apply(
                            request.getExecutor(),
                            request.getDataset(),
                            request.getVersionId(),
                            wrapper.getCommitted().getCommit(),
                            request.getReplyTo(),
                            request.getErrorTo());

                        dataset.tell(publish);

                        return Behaviors.stopped();
                    })
                    .onMessage(ErrorMessageWrapper.class, (actor, wrapper) -> {
                        request.getErrorTo().tell(wrapper.message);
                        return Behaviors.stopped();
                    })
                    .onMessage(Timeout.class, (actor, timeout) -> Behaviors.stopped())
                    .build();
            });
        });
    }

    private interface Message {

    }

    @Value
    private static class Timeout implements Message {

    }

    @Value
    private static class CommittedDatasetVersionWrapper implements Message {

        private final CommittedDatasetVersion committed;

    }

    @Value
    private static class ErrorMessageWrapper implements Message {

        private final ErrorMessage message;

    }

}
