package maquette.controller.domain;

import java.util.Map;

import com.google.common.collect.Maps;
import com.thedeanda.lorem.Lorem;

import lombok.AllArgsConstructor;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.governance.GovernanceProperties;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.project.ProjectPrivilege;

@AllArgsConstructor(staticName = "apply")
public final class SampleDataProvider {

    private final CoreApplication app;

    private final Map<String, Object> variables;

    public static SampleDataProvider apply(CoreApplication app) {
        return apply(app, Maps.newHashMap());
    }

    public void initialize() {
        User alice = AuthenticatedUser.apply("alice", "alice", "team-a", "team-c");
        User bob = AuthenticatedUser.apply("bob", "bob", "team-a", "team-b");
        User clair = AuthenticatedUser.apply("clair", "clair", "team-b");
        User debra = AuthenticatedUser.apply("debra", "debra", "team-c");

        createProject(alice, "animals", true);
        addDataset(alice, "pigs", false);
        addData(alice, getPigsSample());
        addData(alice, getPigsSample());

        createProject(bob, "learning ml", true);
        addDataset(bob, "ml-samples", true);
        addData(bob, getMLSamples());

        createProject(clair, "imdb", true);
        addDataset(clair, "episodes", false);
        addData(clair, getEpisodesSamples());

        createProject(debra, "twitter-analysis", false);
        changeProjectOwner(debra, RoleAuthorization.apply("team-c"));
        addProjectConsumer(debra, clair);
        addProjectConsumer(debra, bob);
        addDataset(debra, "data", false);
        addData(debra, getTwitterSamples());

    }

    private void addData(User user, Records data) {
        Operators.suppressExceptions(() -> {
            String project = getVariable("project");
            String dataset = getVariable("dataset");

            app
                .datasets()
                .putData(user, ResourcePath.apply(project, dataset), data.getSource(), Lorem.getWords(6, 12))
                .toCompletableFuture()
                .get();
        });
    }

    private void addDataset(User user, String name, boolean isPrivate) {
        addDataset(user, name, isPrivate, Lorem.getWords(4));
    }

    private void addDataset(User user, String name, boolean isPrivate, String description) {
        Operators.suppressExceptions(() -> {
            variables.put("dataset", name);
            String project = getVariable("project");

            app
                .datasets()
                .createDataset(
                    user,
                    ResourcePath.apply(project, name),
                    Markdown.apply(description + ". " + Lorem.getWords(20, 50)),
                    isPrivate,
                    GovernanceProperties.apply())
                .toCompletableFuture()
                .get();
        });
    }

    private void addProjectConsumer(User user, User forUser) {
        Operators.suppressExceptions(() -> {
            String project = getVariable("project");

            app
                .projects()
                .grantAccess(
                    user, ResourceName.apply(project), ProjectPrivilege.CONSUMER,
                    UserAuthorization.apply(forUser.getUserId().getId()))
                .toCompletableFuture()
                .get();
        });
    }

    private void changeProjectOwner(User user, Authorization owner) {
        Operators.suppressExceptions(() -> {
            String project = getVariable("project");

            app
                .projects()
                .changeOwner(user, ResourceName.apply(project), owner);
        });
    }

    private void createProject(User user, String name, boolean isPrivate) {
        createProject(user, name, isPrivate, Lorem.getWords(7));
    }

    private void createProject(User user, String name, boolean isPrivate, String description) {
        Operators.suppressExceptions(() -> {
            variables.put("project", name);

            app
                .projects()
                .create(user, ResourceName.apply(name), Markdown.apply(description + ". " + Lorem.getWords(20, 50)), isPrivate)
                .toCompletableFuture()
                .get();
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T getVariable(String name) {
        return (T) variables.get(name);
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
