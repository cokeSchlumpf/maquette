import datasets, { types as datasetsTypes } from './datasets/actions';
import user, { types as userTypes } from './user/actions';

export const types = {
    datasets: datasetsTypes,
    user: userTypes
};

export default {
    datasets,
    user
}