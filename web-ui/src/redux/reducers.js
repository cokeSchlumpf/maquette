import { combineReducers } from 'redux-immutable';
import { connectRouter } from 'connected-react-router/immutable'

import app from './app/reducers';
import services from './services/reducers';
import views from './views/reducers';

export default (history) => {
    return combineReducers({
        router: connectRouter(history),

        app,
        services,
        views
    });
}