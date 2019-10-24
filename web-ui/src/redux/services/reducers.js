import { combineReducers } from 'redux-immutable';

import datasets from './datasets/reducers'
import projects from './projects/reducers'
import user from './user/reducers'

export default combineReducers({
    datasets,
    projects,
    user
});