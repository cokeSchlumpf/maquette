import { combineReducers } from 'redux-immutable';

import browse from '../../components/views/Browse/redux/reducers';
import project from '../../components/views/Project/redux/reducers';

export default combineReducers({
    browse,
    project
});