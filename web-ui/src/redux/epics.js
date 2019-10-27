import { combineEpics } from 'redux-observable';

import app from './app/epics';
import components from './components/epics';
import services from './services/epics';
import views from './views/epics';

export default combineEpics(app, components, services, views);