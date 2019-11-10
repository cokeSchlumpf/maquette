import _ from 'lodash';
import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({
    dataset: null,
    datasetLoading: false,
    versions: [],
    versionsLoading: false
});

const get = (state) => {
    return state.setIn(['datasetLoading'], true);;
};

const getFail = (state) => {
    return state
        .setIn(['datasetLoading'], false);
};

const getSuccess = (state, payload) => {
    return state
        .setIn(['dataset'], payload)
        .setIn(['datasetLoading'], false);
};

const listVersions = (state) => {
    return state.setIn(['versionsLoading'], true);
};

const listVersionsFail = (state) => {
    return state
        .setIn(['versionsLoading'], false);
};

const listVersionsSuccess = (state, payload) => {
    return state
        .setIn(['versions'], _.get(payload, 'versions', []))
        .setIn(['versionsLoading'], false);
};

export default (state = initialState, action) => {
    switch (action.type) {
        case types.GET:
            return get(state, action.payload);
        case types.GET_FAIL:
            return getFail(state, action.payload);
        case types.GET_SUCCESS:
            return getSuccess(state, action.payload);
        case types.LIST_VERSIONS:
            return listVersions(state, action.payload);
        case types.LIST_VERSIONS_FAIL:
            return listVersionsFail(state, action.payload);
        case types.LIST_VERSIONS_SUCCESS:
            return listVersionsSuccess(state, action.payload);
        default:
            return state;
    }
};
