import { combineEpics } from "redux-observable";

import datasets from './datasets/epics'
import projects from './projects/epics'
import user from './user/epics'

export default combineEpics(
    datasets,
    projects,
    user);