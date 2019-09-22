import 'rxjs';

import actions, { types } from '../actions';

import _ from 'lodash';

export default [
    // app.INIT -> services.login.INIT
    /*
    action$ => action$
        .ofType(types.app.INIT)
        .flatMap(action => [
            actions.services.user.init()
        ])
     */
]