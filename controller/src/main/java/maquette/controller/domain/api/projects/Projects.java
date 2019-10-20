package maquette.controller.domain.api.projects;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import akka.Done;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;
import maquette.controller.domain.values.project.ProjectPrivilege;

public interface Projects {

    /**
     * Changes the description of an existing project.
     *
     * @param executor
     *     The user who executes the command
     * @param project
     *     The project which should be changed
     * @param description
     *     The new description
     * @return Updated project details
     */
    CompletionStage<ProjectDetails> changeDescription(User executor, ResourceName project, Markdown description);

    /**
     * Change the owner of an existing project.
     *
     * @param executor
     *     The user who executes the command
     * @param project
     *     The project which should be changed
     * @param owner
     *     The new owner of the project
     * @return Updated project details
     */
    CompletionStage<ProjectDetails> changeOwner(User executor, ResourceName project, Authorization owner);

    /**
     * Change the privacy setting of an existing project.
     *
     * @param executor
     *     The user who executes the command
     * @param project
     *     The project which should be changed
     * @param isPrivate
     *     The new value whether the project should be private or not
     * @return Updated project details
     */
    CompletionStage<ProjectDetails> changePrivacy(User executor, ResourceName project, boolean isPrivate);

    /**
     * Creates a new project.
     *
     * @param executor
     *     The user who executes the command
     * @param project
     *     The project which should be created
     * @param description
     *     The initial description of the project
     * @param isPrivate
     *     Whether the project is private or not
     * @return Details of the newly created project
     */
    CompletionStage<ProjectDetails> create(User executor, ResourceName project, Markdown description, boolean isPrivate);

    /**
     * @param executor
     *     The user who executes the command
     * @param project
     *     The project which should be deleted
     * @return Just Done
     */
    CompletionStage<Done> delete(User executor, ResourceName project);

    /**
     * @param executor
     *     The user who executes the query
     * @param project
     *     The project which datasets should be returned
     * @return A list of datasets
     */
    CompletionStage<Set<DatasetDetails>> getDatasets(User executor, ResourceName project);

    /**
     * @param executor
     *     The user who executes the query
     * @param project
     *     The project which details should be returned
     * @return The current details of the project
     */
    CompletionStage<ProjectDetails> getDetails(User executor, ResourceName project);

    /**
     * @param executor
     *     The user who executes the command
     * @param project
     *     The project which should be changed
     * @param grant
     *     The privilege which should be granted
     * @param grantFor
     *     The authorization which should receive the privilege
     * @return Updated project details
     */
    CompletionStage<GrantedAuthorization> grantAccess(
        User executor, ResourceName project, ProjectPrivilege grant, Authorization grantFor);

    /**
     * @param executor
     *     The user who executes the command
     * @param project
     *     The project which should be changed
     * @param revoke
     *     The privilege which should be revoked from the project
     * @param revokeFrom
     *     The authorization which should loose the privilege
     * @return Updated project details
     */
    CompletionStage<GrantedAuthorization> revokeAccess(
        User executor, ResourceName project, ProjectPrivilege revoke, Authorization revokeFrom);

}
