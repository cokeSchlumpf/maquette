package maquette.controller.domain.entities.dataset.services;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import lombok.Value;
import maquette.controller.domain.entities.dataset.protocol.VersionMessage;
import maquette.controller.domain.entities.dataset.protocol.queries.GetDetails;
import maquette.controller.domain.entities.dataset.protocol.queries.GetVersionDetails;
import maquette.controller.domain.entities.dataset.protocol.results.GetDetailsResult;
import maquette.controller.domain.entities.dataset.protocol.results.GetVersionDetailsResult;
import maquette.controller.domain.values.core.ErrorMessage;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionInfo;
import maquette.controller.domain.values.dataset.VersionTagInfo;

public final class CollectDetails {

    private CollectDetails() {

    }

    public static Behavior<Message> create(
        List<VersionTagInfo> versions,
        Map<UID, ActorRef<VersionMessage>> versionActors,
        GetDetails request,
        DatasetDetails datasetDetails) {

        return Behaviors.setup(ctx -> {
            final ActorRef<GetVersionDetailsResult> getVersionDetailsResultAdapter =
                ctx.messageAdapter(GetVersionDetailsResult.class, GetVersionDetailsResultWrapper::new);

            final ActorRef<ErrorMessage> errorMessageAdapter =
                ctx.messageAdapter(ErrorMessage.class, ErrorMessageWrapper::new);

            versionActors.forEach((uid, version) -> {
                final GetVersionDetails msg = GetVersionDetails.apply(
                    request.getDataset(), uid, getVersionDetailsResultAdapter, errorMessageAdapter);

                version.tell(msg);
            });

            return Behaviors.withTimers(scheduler -> {
                scheduler.startSingleTimer(Timeout.class, new Timeout(), Duration.ofSeconds(5));
                return collecting(versions, request, datasetDetails, Lists.newArrayList(), versionActors.size());
            });
        });
    }

    private static Behavior<Message> collecting(
        final List<VersionTagInfo> versions,
        final GetDetails request,
        final DatasetDetails datasetDetails,
        final List<VersionInfo> infos,
        final int remaining) {

        if (remaining <= 0) {
            final DatasetDetails details = datasetDetails.withVersions(Sets.newHashSet(infos));
            request.getReplyTo().tell(GetDetailsResult.apply(details));
            return Behaviors.stopped();
        } else {
            return Behaviors
                .receive(Message.class)
                .onMessage(
                    ErrorMessageWrapper.class,
                    (ctx, error) -> collecting(versions, request, datasetDetails, infos, remaining - 1))
                .onMessage(GetVersionDetailsResultWrapper.class, (ctx, wrapper) -> {
                    final VersionDetails details = wrapper.result.getDetails();
                    final Optional<VersionTagInfo> versionNumber =
                        versions.stream().filter(p -> p.getId().equals(details.getVersionId())).findFirst();

                    VersionInfo info = VersionInfo.apply(
                        details.getVersionId(),
                        details.getLastModified(),
                        details.getModifiedBy(),
                        details.getRecords(),
                        versionNumber.map(VersionTagInfo::getVersion).orElse(null));

                    infos.add(info);
                    return collecting(versions, request, datasetDetails, infos, remaining - 1);
                })
                .onMessage(
                    Timeout.class,
                    (ctx, timeout) -> collecting(versions, request, datasetDetails, infos, 0))
                .build();
        }
    }

    private interface Message {

    }

    @Value
    private static class Timeout implements Message {

    }

    @Value
    private static class ErrorMessageWrapper implements Message {

        private final ErrorMessage message;

    }

    @Value
    private static class GetVersionDetailsResultWrapper implements Message {

        private final GetVersionDetailsResult result;

    }

}
