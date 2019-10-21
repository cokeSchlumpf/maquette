package maquette.cucumber;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.collect.Lists;

import cucumber.api.java.en.Given;
import io.cucumber.datatable.DataTable;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.commands.EAuthorizationType;
import maquette.controller.adapters.cli.commands.projects.ChangeProjectOwnerCmd;
import maquette.controller.adapters.cli.commands.projects.CreateProjectCmd;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor
public final class ProjectSteps {

    TestContext ctx;

    @Given("{string} changes owner of project to role {string}")
    public void changes_owner_of_project_to_role(String userName, String roleName) throws ExecutionException, InterruptedException {
        User user = ctx.getUser(userName);
        ResourceName project = ResourceName.apply(ctx.getVariable("project", String.class));

        ctx
            .getSetup()
            .getApp()
            .projects()
            .changeOwner(user, project, RoleAuthorization.apply(roleName))
            .toCompletableFuture()
            .get();
    }

    @Given("{string} creates a project {string}")
    public void creates_a_project(String userName, String projectName) throws ExecutionException, InterruptedException {
        User user = ctx.getUser(userName);

        ctx
            .getSetup()
            .getApp()
            .projects()
            .create(user, ResourceName.apply(projectName), Markdown.apply(), false)
            .toCompletableFuture()
            .get();

        ctx.setVariable("project", projectName);
        ctx.setVariable("user", userName);
    }

    @Given("{string} creates a private project {string}")
    public void creates_a_private_project(String userName, String projectName) throws ExecutionException, InterruptedException {
        User user = ctx.getUser(userName);

        ctx
            .getSetup()
            .getApp()
            .projects()
            .create(user, ResourceName.apply(projectName), Markdown.apply(), true)
            .toCompletableFuture()
            .get();

        ctx.setVariable("project", projectName);
        ctx.setVariable("user", userName);
    }

    @Given("we have the following role-owned projects")
    public void we_have_the_following_additional_role_owned_projects(DataTable dataTable)
        throws ExecutionException, InterruptedException {

        List<List<String>> data = Lists.newArrayList(dataTable.asLists());
        data.remove(0);

        for (List<String> project : data) {
            ResourceName name = ResourceName.apply(project.get(0));
            CreateProjectCmd
                .apply(name, Markdown.apply(""), project.get(2).equals("yes"))
                .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp())
                .thenCompose(result -> ChangeProjectOwnerCmd
                    .apply(name, EAuthorizationType.ROLE, project.get(1))
                    .run(ctx.getSetup().getAdminUser(), ctx.getSetup().getApp()))
                .toCompletableFuture()
                .get();
        }
    }

}
