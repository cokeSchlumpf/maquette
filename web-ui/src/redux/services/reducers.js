import { combineReducers } from 'redux-immutable';

import dataset from './dataset/reducers'
import datasets from './datasets/reducers'
import project from './project/reducers'
import projects from './projects/reducers'
import user from './user/reducers'

export default combineReducers({
    dataset,
    datasets,
    project,
    projects,
    user
});