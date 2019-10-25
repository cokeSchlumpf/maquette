import {
    Route,
    Switch,
} from 'react-router-dom';

import App from './components/App';

import Browse from './components/views/Browse';
import Dashboard from './components/views/Dashboard';
import NotFound from './components/views/NotFound';

import React from 'react';
import { ConnectedRouter as Router } from 'connected-react-router/immutable'
import { createBrowserHistory } from 'history'

export const history = createBrowserHistory();

const Routes = (props) => (
    <Router history={history}>
        <App>
            <Switch>
                <Route path='/' exact component={Dashboard} />
                <Route path='/browse' component={Browse} />
                <Route path='*' component={NotFound} />
            </Switch>
        </App>
    </Router>
);

export default Routes;
