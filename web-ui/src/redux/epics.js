import { combineEpics } from 'redux-observable';

import _ from 'lodash';
import app from './app/epics';
import services from './services/epics';
import views from './views/epics';

let epics = _.concat(
    app,
    services,
    views);

console.log(epics);

export default combineEpics(app, services);