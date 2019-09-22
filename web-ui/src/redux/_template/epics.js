import 'rxjs';

import { types } from './actions';

export const sampleEpic = (action$, store) => action$
    .ofType(types.FOO)
    .mapTo({
        type: types.LOREM,
        payload: {
            foo: 'bar'
        }
    });

export default [
    sampleEpic
]