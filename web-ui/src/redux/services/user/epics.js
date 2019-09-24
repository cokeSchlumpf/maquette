import { ofType, combineEpics } from 'redux-observable'
import { mergeMap } from "rxjs/operators";

import { types } from './actions';
import { fetchFail, fetchSuccess } from './actions';

import FetchClient from '../../../utils/fetch-client';

const service = new FetchClient('/api/v1/about/user');

export const fetchEpic = (action$, store) => action$.pipe(
    ofType(types.FETCH),
    mergeMap(action => service
        .read()
        .then(fetchSuccess)
        .catch(fetchFail)));

export default combineEpics(
    fetchEpic
);