import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({
    name: 'anonymous',
    roles: [],
    notifications: 0
});

const fetchSuccess = (state, payload) => {
    return fromJS(payload);
};

export default (state = initialState, action) => {
    switch (action.type) {
        case types.FETCH_SUCCESS:
            return fetchSuccess(state, action.payload);
        default:
            return state;
    }
};