package maquette.cucumber;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import io.cucumber.datatable.DataTable;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.iam.AuthenticatedUser;
import maquette.controller.domain.values.iam.RoleAuthorization;

@Value
@AllArgsConstructor
public class GeneralSetupSteps {

    private TestContext ctx;

    @Given("user {string} has the following datasets in his private namespace")
    public void userHasTheFollowingDatasetsInHisPrivateNamespace(String user, DataTable dataTable) {

    }

    @Given("we have the following additional role-owned namespaces")
    public void we_have_the_following_additional_role_owned_namespaces(DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
        // throw new cucumber.api.PendingException();
    }

    @Given("namespace {string} contains the following datasets")
    public void namespace_contains_the_following_datasets(String string, DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
        // throw new cucumber.api.PendingException();
    }

    @Given("we have the following users")
    public void we_have_the_following_users(DataTable dataTable) {
        List<List<String>> data = Lists.newArrayList(dataTable.asLists());
        data.remove(0);

        for (List<String> user : data) {
            Set<String> split = Lists
                .newArrayList(user.get(1).split(","))
                .stream()
                .map(String::trim)
                .collect(Collectors.toSet());

            ctx.withUser(AuthenticatedUser.apply(user.get(0), user.get(0), split));
        }
    }

}
