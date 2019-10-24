import { ofType, combineEpics } from 'redux-observable'
import { mergeMap } from "rxjs/operators";

import { types } from './actions';
import { fetchFail, fetchSuccess } from './actions';

import FetchClient from '../../../utils/fetch-client';

const service = new FetchClient('/api/v1/commands');

export const fetchEpic = (action$, store) => action$.pipe(
    ofType(types.FETCH),
    mergeMap(action => service
        .command("user show")
        .then(fetchSuccess)
        .catch(fetchFail)));

export default combineEpics(
    fetchEpic
);