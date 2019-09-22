import { applyMiddleware, compose, createStore } from 'redux';
import { routerMiddleware } from 'connected-react-router/immutable'
import { createEpicMiddleware } from 'redux-observable';

import { history } from '../routes';
import { init } from './app/actions';
import { createLogger } from 'redux-logger';
import rootEpic from './epics';
import rootReducer from './reducers';

const epicMiddleware = createEpicMiddleware(rootEpic);

const composeEnhancers = (typeof window !== 'undefined' && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__) || compose;

const logger = createLogger({
    stateTransformer: (state) => {
        const newState = {};
        const stateObj = state.toObject();

        for (const i of Object.keys(stateObj)) {
            if (!!stateObj.toJS) {
                newState[i] = stateObj[i].toJS();
            } else {
                newState[i] = stateObj[i];
            }
        }

        return newState;
    }
});

const store = createStore(
    rootReducer(history),
    composeEnhancers(
        applyMiddleware(epicMiddleware, logger, routerMiddleware(history))
    )
);

store.dispatch(init());

export default store;