import { ofType, combineEpics } from 'redux-observable'
import { mapTo } from "rxjs/operators";

import { types } from './actions';

export const sampleEpic = action$ => action$.pipe(
    ofType(types.FOO),
    mapTo({ type: types.LOREM, payload: { foo: 'foo' }}));

export default combineEpics(
    sampleEpic
);