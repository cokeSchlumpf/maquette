import { combineEpics, ofType } from 'redux-observable'
import { flatMap } from "rxjs/operators";

import actions, { types } from '../actions';

export default combineEpics(
    action$ => action$
        .pipe(
            ofType(types.app.INIT),
            flatMap(action => [actions.services.user.fetch()]))
);