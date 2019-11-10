import { ofType, combineEpics } from 'redux-observable'
import { mergeMap } from "rxjs/operators";

import { types, getFail, getSuccess, listVersionsFail, listVersionsSuccess } from './actions';

import FetchClient from "../../../utils/fetch-client";

const service = new FetchClient('/api/v1/commands');

export const getEpic = (action$, store) => action$.pipe(
    ofType(types.GET),
    mergeMap(action => service
        .command("dataset show", action.payload)
        .then(getSuccess)
        .catch(getFail)));

export const listVersionsEpic = (action$, store) => action$.pipe(
    ofType(types.LIST_VERSIONS),
    mergeMap(action => service
        .command("dataset versions", action.payload)
        .then(listVersionsSuccess)
        .catch(listVersionsFail)));

export default combineEpics(
    getEpic,
    listVersionsEpic
);