package maquette.controller.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.governance.GovernanceProperties;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor(staticName = "apply")
public class SampleDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SampleDataProvider.class);

    private final CoreApplication app;

    public void initialize() {
        User alice = AuthenticatedUser.apply("alice", "alice", "team-a", "team-c");
        User bob = AuthenticatedUser.apply("bob", "bob", "team-a", "team-b");
        User clair = AuthenticatedUser.apply("clair", "clair", "team-b");
        User debra = AuthenticatedUser.apply("debra", "debra", "team-c");

        Operators.suppressExceptions(() -> {
            app
                .projects()
                .create(alice, ResourceName.apply("alice"), Markdown.apply("# Alice' Playground"), true)
                .toCompletableFuture()
                .get();

            app
                .datasets()
                .createDataset(alice, ResourcePath.apply("alice", "pigs"), Markdown.apply(), false, GovernanceProperties.apply())
                .thenCompose(details -> {
                    LOG.info(String.format("Created dataset %s/%s", alice.getUserId(), details.getDataset()));

                    return app
                        .datasets()
                        .putData(alice, details.getDataset(), getPigsSample().getSource(), "Initial data")
                        .thenCompose(
                            versionTag -> {
                                LOG.debug(String.format("... created version %s with sample data", versionTag));

                                return app
                                    .datasets()
                                    .putData(alice, details.getDataset(), getPigsSample().getSource(), "new version")
                                    .thenAccept(versionTag1 -> {
                                        LOG.debug(String.format("... created version %s with sample data", versionTag1));
                                    });
                            });
                })
                .toCompletableFuture()
                .get();

            app
                .projects()
                .create(bob, ResourceName.apply("bob"), Markdown.apply("# Bobs Playground"), true)
                .toCompletableFuture()
                .get();

            app
                .datasets()
                .createDataset(bob, ResourcePath.apply("bob", "ml-samples"), Markdown.apply(), true, GovernanceProperties.apply())
                .thenCompose(details -> {
                    LOG.info(String.format("Created private dataset %s/%s", bob.getUserId(), details.getDataset()));

                    return app
                        .datasets()
                        .putData(bob, details.getDataset(), getMLSamples().getSource(), "A few samples I found")
                        .thenAccept(versionTag -> {
                            LOG.debug(String.format("... created version %s with sample data", versionTag));
                        });
                })
                .toCompletableFuture()
                .get();

            app
                .projects()
                .create(clair, ResourceName.apply("clair"), Markdown.apply("# Clairs Playground"), true)
                .toCompletableFuture()
                .get();

            app
                .datasets()
                .createDataset(
                    clair,
                    ResourcePath.apply("clair", "episodes"),
                    Markdown.apply(),
                    false,
                    GovernanceProperties.apply())
                .thenCompose(details -> {
                    LOG.info(String.format("Created dataset %s/%s", clair.getUserId(), details.getDataset()));

                    return app
                        .datasets()
                        .putData(clair, details.getDataset(), getEpisodesSamples().getSource(), "Some episodes")
                        .thenAccept(versionTag -> {
                            LOG.debug(String.format("... created version %s with sample data", versionTag));
                        });
                })
                .toCompletableFuture()
                .get();

            app
                .projects()
                .create(
                    debra,
                    ResourceName.apply("twitter-analysis"),
                    Markdown.apply("# Twitter Analysis\n\nFoo Bar"),
                    false)
                .thenCompose(details -> {
                    LOG.info(String.format("Created project %s", details.getName()));

                    return app
                        .projects()
                        .changeOwner(debra, ResourceName.apply("twitter-analysis"), RoleAuthorization.apply("team-c"));
                })
                .thenCompose(details -> {
                    LOG.info(String.format("... changed owner to %s", details.getAcl().getOwner().getAuthorization()));

                    return app
                        .datasets()
                        .createDataset(
                            debra,
                            ResourcePath.apply("twitter-analysis", "data"),
                            Markdown.apply(),
                            false,
                            GovernanceProperties.apply());
                })
                .thenCompose(details -> {
                    LOG.info(String.format(
                        "... Created dataset %s/%s",
                        details.getDataset().getProject(),
                        details.getDataset().getName()));

                    return app
                        .datasets()
                        .putData(debra, details.getDataset(), getTwitterSamples().getSource(), "initial data")
                        .thenAccept(versionTag -> {
                            LOG.debug(String.format("... ... created version %s with sample data", versionTag));
                        });
                })
                .toCompletableFuture()
                .get();
        });
    }

    private Records getEpisodesSamples() {
        return Records.fromInputStream(this.getClass().getResourceAsStream("/samples/episodes.avro"));
    }

    private Records getMLSamples() {
        return Records.fromInputStream(this.getClass().getResourceAsStream("/samples/mlsamples.avro"));
    }

    private Records getPassengerSamples() {
        return Records.fromInputStream(this.getClass().getResourceAsStream("/samples/passengers.avro"));
    }

    private Records getPigsSample() {
        return Records.fromInputStream(this.getClass().getResourceAsStream("/samples/pigs.avro"));
    }

    private Records getTfxSamples() {
        return Records.fromInputStream(this.getClass().getResourceAsStream("/samples/tfx.avro"));
    }

    private Records getTwitterSamples() {
        return Records.fromInputStream(this.getClass().getResourceAsStream("/samples/twitter.avro"));
    }

    private Records getUserdataSample() {
        return Records.fromInputStream(this.getClass().getResourceAsStream("/samples/userdata.avro"));
    }

}
