import browse, { types as browseTypes } from '../../components/views/Browse/redux/actions';
import dataset, { types as datasetTypes } from '../../components/views/Dataset/redux/actions';
import project, { types as projectTypes } from '../../components/views/Project/redux/actions';

export const types = {
    browse: browseTypes,
    dataset: datasetTypes,
    project: projectTypes
};

export default {
    browse,
    dataset,
    project
}