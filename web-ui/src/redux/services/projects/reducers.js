import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({
    projectsLoading: false,
    projects: [],
    project: undefined,
    projectLoading: false
});

const find = (state) => {
    return state.setIn(['projectsLoading'], true);
};

const findSuccess = (state, payload) => {
    return state
        .setIn(['projects'], payload.projects)
        .setIn(['projectsLoading'], false);
};

const list = (state) => {
    return state.setIn(['projectsLoading'], true);
};

const listSuccess = (state, payload) => {
    return state
        .setIn(['projects'], payload.projects)
        .setIn(['projectsLoading'], false);
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