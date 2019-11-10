import { combineEpics } from "redux-observable";

import dataset from './dataset/epics'
import datasets from './datasets/epics'
import project from './project/epics'
import projects from './projects/epics'
import user from './user/epics'

export default combineEpics(
    dataset,
    datasets,
    project,
    projects,
    user);