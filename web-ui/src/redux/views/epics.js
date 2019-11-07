import { combineEpics } from "redux-observable";

import browse from '../../components/views/Browse/redux/epics';
import project from '../../components/views/Project/redux/epics';

export default combineEpics(
    browse,
    project);