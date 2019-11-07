import browse, { types as browseTypes } from '../../components/views/Browse/redux/actions';
import project, { types as projectTypes } from '../../components/views/Project/redux/actions';

export const types = {
    browse: browseTypes,
    project: projectTypes
};

export default {
    browse,
    project
}