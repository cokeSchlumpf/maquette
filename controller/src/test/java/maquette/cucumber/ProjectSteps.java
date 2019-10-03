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

@AllArgsConstructor
public final class ProjectSteps {

    TestContext ctx;

    @Given("we have the following role-owned projects")
    public void we_have_the_following_additional_role_owned_projects(DataTable dataTable)
        throws ExecutionException, InterruptedException {

        List<List<String>> data = Lists.newArrayList(dataTable.asLists());
        data.remove(0);

        for (List<String> project : data) {
            ResourceName name = ResourceName.apply(project.get(0));
            CreateProjectCmd
                .apply(name, Markdown.apply(""), project.get(2).equals("yes"))
                .run(ctx.getSetup().getDefaultUser(), ctx.getSetup().getApp())
                .thenCompose(result -> ChangeProjectOwnerCmd
                    .apply(name, EAuthorizationType.ROLE, project.get(1))
                    .run(ctx.getSetup().getDefaultUser(), ctx.getSetup().getApp()))
                .toCompletableFuture()
                .get();
        }
    }

}
