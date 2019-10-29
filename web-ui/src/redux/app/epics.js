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
            ofType(types.views.browse.CLEAR_SEARCH),
            flatMap(action => [
                actions.services.datasets.list(),
                actions.services.projects.list()
            ])),

    action$ => action$
        .pipe(
            ofType(types.views.browse.SEARCH),
            flatMap(action => [
                actions.services.datasets.find(action.payload.query),
                actions.services.projects.find(action.payload.query)
            ])),

    action$ => action$
        .pipe(
            ofType("@@router/LOCATION_CHANGE"),
            filter(($action) => $action.payload.location.pathname === "/browse"),
            mergeMap(action => [
                actions.services.datasets.list(),
                actions.services.projects.list()
            ])
        )
);