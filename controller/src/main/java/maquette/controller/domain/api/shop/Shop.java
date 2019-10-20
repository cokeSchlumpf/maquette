package maquette.controller.domain.api.shop;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.iam.User;
import maquette.controller.domain.values.project.ProjectDetails;

public interface Shop {

    /**
     * Query accessible (not private) datasets.
     *
     * @param executor The executor which executes the query.
     * @param query The filter query.
     * @return A list of datasets.
     */
    CompletionStage<Set<DatasetDetails>> findDatasets(User executor, String query);

    /**
     * Returns a list of Datasets where the executor is active member.
     *
     * @param executor The user which executes the request.
     * @return A list of datasets.
     */
    CompletionStage<Set<DatasetDetails>> listDatasets(User executor);

    /**
     * Returns a list of Projects where the executor is active member.
     *
     * @param executor The user which executes the request.
     * @return A list of projects
     */
    CompletionStage<Set<ProjectDetails>> listProjects(User executor);

}
