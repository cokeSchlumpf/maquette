import datasets, { types as datasetsTypes } from './datasets/actions';
import projects, { types as projectsTypes } from './projects/actions';
import user, { types as userTypes } from './user/actions';

export const types = {
    datasets: datasetsTypes,
    projects: projectsTypes,
    user: userTypes
};

export default {
    datasets,
    projects,
    user
}