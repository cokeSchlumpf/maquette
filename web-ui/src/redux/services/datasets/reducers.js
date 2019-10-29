import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({
    datasetsLoading: false,
    datasets: [],
    dataset: undefined
});

const find = (state) => {
    return state.setIn(['datasetsLoading'], true);
};

const findSuccess = (state, payload) => {
    return state
        .setIn(['datasets'], payload.datasets)
        .setIn(['datasetsLoading'], false);
};

const list = (state) => {
    return state.setIn(['datasetsLoading'], true);
};

const listSuccess = (state, payload) => {
    return state
        .setIn(['datasets'], payload.datasets)
        .setIn(['datasetsLoading'], false);
};

export default (state = initialState, action) => {
    switch (action.type) {
        case types.FIND:
            return find(state, action.payload);
        case types.FIND_SUCCESS:
            return findSuccess(state, action.payload);
        case types.LIST:
            return list(state, action.payload);
        case types.LIST_SUCCESS:
            return listSuccess(state, action.payload);
        default:
            return state;
    }
};