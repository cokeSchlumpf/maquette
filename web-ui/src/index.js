import { Provider } from 'react-redux'
import React from 'react';
import ReactDOM from 'react-dom';
import Routes from './routes';
import store from './redux/store';

import * as serviceWorker from './serviceWorker';

ReactDOM.render(
    <Provider store={store}>
            <Routes />
    </Provider>,
    document.getElementById('root')
);

serviceWorker.unregister();