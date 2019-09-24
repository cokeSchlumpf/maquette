import { combineReducers } from 'redux-immutable';

import datasets from './datasets/reducers'
import user from './user/reducers'

export default combineReducers({
    datasets,
    user
});