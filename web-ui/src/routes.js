import {
    Route,
    Switch,
} from 'react-router-dom';

import App from './components/App';
import NotFound from './components/NotFound';
import Sample from './components/Sample';

import React from 'react';
import { ConnectedRouter as Router } from 'connected-react-router/immutable'
import { createBrowserHistory } from 'history'

export const history = createBrowserHistory();

const Routes = (props) => (
    <Router history={history}>
        <App>
            <Switch>
                <Route path='/' component={Sample} />
                <Route path='*' component={NotFound} />
            </Switch>
        </App>
    </Router>
);

export default Routes;
