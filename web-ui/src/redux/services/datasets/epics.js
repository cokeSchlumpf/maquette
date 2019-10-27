import { ofType, combineEpics } from 'redux-observable'
import { mergeMap } from "rxjs/operators";

import { types } from './actions';
import { findFail, findSuccess, listFail, listSuccess } from './actions';

import FetchClient from "../../../utils/fetch-client";

const service = new FetchClient('/api/v1/commands');

export const findEpic = (action$, store) => action$.pipe(
    ofType(types.FIND),
    mergeMap(action => service
        .command("shop find datasets", action.payload)
        .then(findSuccess)
        .catch(findFail)));

export const listEpic = (action$, store) => action$.pipe(
    ofType(types.LIST),
    mergeMap(action => service
        .command("shop list datasets")
        .then(listSuccess)
        .catch(listFail)));

export default combineEpics(
    findEpic,
    listEpic
);