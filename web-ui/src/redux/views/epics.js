import { combineEpics } from "redux-observable";

import browse from '../../components/views/Browse/redux/epics';

export default combineEpics(
    browse);