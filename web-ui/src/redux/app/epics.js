import { combineEpics, ofType } from 'redux-observable'
import {flatMap, mergeMap, filter} from "rxjs/operators";

import actions, { types } from '../actions';

import FetchClient from "../../utils/fetch-client";

const service = new FetchClient('/api/v1/commands');

export default combineEpics(
    action$ => action$.pipe(
        ofType(types.app.FETCH_SETTINGS),
        mergeMap(action => service
            .command("about")
            .then(actions.app.fetchSettingsSuccess)
            .catch(actions.app.fetchSettingsFail))),

    action$ => action$
        .pipe(
            ofType(types.app.INIT),
            flatMap(action => [
                actions.services.user.fetch(),
                actions.app.fetchSettings()
            ])),

    action$ => action$
        .pipe(
            ofType("@@router/LOCATION_CHANGE"),
            filter(($action) => $action.payload.location.pathname === "/browse"),
            mergeMap(action => [
                actions.services.projects.list()
            ])
        )
);