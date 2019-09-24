import { applyMiddleware, compose, createStore } from 'redux';
import { routerMiddleware } from 'connected-react-router/immutable'
import { createEpicMiddleware } from 'redux-observable';

import { history } from '../routes';
import { init } from './app/actions';
import { createLogger } from 'redux-logger';
import rootEpic from './epics';
import rootReducer from './reducers';

const composeEnhancers = (typeof window !== 'undefined' && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__) || compose;

const epicMiddleware = createEpicMiddleware();

const logger = createLogger({
    stateTransformer: (state) => !!state.toJS ? state.toJS() : state
});

const store = createStore(
    rootReducer(history),
    composeEnhancers(
        applyMiddleware(epicMiddleware, logger, routerMiddleware(history))
    )
);

epicMiddleware.run(rootEpic);
store.dispatch(init());

export default store;