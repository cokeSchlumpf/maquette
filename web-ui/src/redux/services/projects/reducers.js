import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({
    projects: [],
    project: undefined
});

const findSuccess = (state, payload) => {
    return fromJS(payload)
};

const listSuccess = (state, payload) => {
    return fromJS(payload);
};

export default (state = initialState, action) => {
    switch (action.type) {
        case types.FIND_SUCCESS:
            return findSuccess(state, action.payload);
        case types.LIST_SUCCESS:
            return listSuccess(state, action.payload);
        default:
            return state;
    }
};