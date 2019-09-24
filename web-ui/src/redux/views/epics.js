import { combineEpics } from "redux-observable";

import assets from '../../views/Assets/redux/epics';

export default combineEpics(
    assets);