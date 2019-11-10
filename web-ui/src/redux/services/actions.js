import dataset, { types as datasetTypes } from './dataset/actions';
import datasets, { types as datasetsTypes } from './datasets/actions';
import project, { types as projectTypes } from './project/actions';
import projects, { types as projectsTypes } from './projects/actions';
import user, { types as userTypes } from './user/actions';

export const types = {
    dataset: datasetTypes,
    datasets: datasetsTypes,
    project: projectTypes,
    projects: projectsTypes,
    user: userTypes
};

export default {
    dataset,
    datasets,
    project,
    projects,
    user
}