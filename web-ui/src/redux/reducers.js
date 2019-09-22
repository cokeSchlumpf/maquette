import { combineReducers } from 'redux-immutable';
import { connectRouter } from 'connected-react-router/immutable'

import serviceA from './_template/reducers';
import serviceB from './_template/reducers';

export default (history) => {
    return combineReducers({ router: connectRouter(history), serviceA, serviceB });
}