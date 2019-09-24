import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({
    id: "anonymous",
    name: "Anonymous"
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