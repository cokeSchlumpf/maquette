import { combineEpics } from "redux-observable";

import datasets from './datasets/epics'
import user from './user/epics'

export default combineEpics(
    datasets,
    user);