import _ from 'lodash';
import {ofType, combineEpics} from 'redux-observable';
import {mergeMap} from "rxjs/operators";

import {types, createAccessRequestFail, createAccessRequestSuccess} from './actions';

import FetchClient from "../../../../utils/fetch-client";

const service = new FetchClient('/api/v1/commands');

export const createDatasetAccessRequestEpic = (action$, store) => action$.pipe(ofType(types.CREATE_ACCESS_REQUEST),
    mergeMap(action => service
        .command("dataset request access", _.get(action, 'payload.request', {}))
        .then(createAccessRequestSuccess)
        .catch(createAccessRequestFail)));

export default combineEpics(createDatasetAccessRequestEpic);