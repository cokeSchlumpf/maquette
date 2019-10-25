import { combineEpics } from "redux-observable";

import assets from '../../components/views/Browse/redux/epics';

export default combineEpics(
    assets);