import { ofType, combineEpics } from 'redux-observable'
import { mergeMap } from "rxjs/operators";

import { types } from './actions';
import { getFail, getSuccess, listDatasetsFail, listDatasetsSuccess } from './actions';

import FetchClient from "../../../utils/fetch-client";

const service = new FetchClient('/api/v1/commands');

export const getEpic = (action$, store) => action$.pipe(
    ofType(types.GET),
    mergeMap(action => service
        .command("project show", action.payload)
        .then(getSuccess)
        .catch(getFail)));

export const listDatasetsEpic = (action$, store) => action$.pipe(
    ofType(types.LIST_DATASETS),
    mergeMap(action => service
        .command("project datasets", action.payload)
        .then(listDatasetsSuccess)
        .catch(listDatasetsFail)));

export default combineEpics(
    getEpic,
    listDatasetsEpic
);