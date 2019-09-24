import { combineEpics } from "redux-observable";

import app from '../../components/App/redux/epics';

export default combineEpics(
    app);