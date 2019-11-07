import datasets, { types as datasetsTypes } from './datasets/actions';
import project, { types as projectTypes } from './project/actions';
import projects, { types as projectsTypes } from './projects/actions';
import user, { types as userTypes } from './user/actions';

export const types = {
    datasets: datasetsTypes,
    project: projectTypes,
    projects: projectsTypes,
    user: userTypes
};

export default {
    datasets,
    project,
    projects,
    user
}