import _ from 'lodash';
import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({
    project: null,
    projectLoading: false,
    datasets: [],
    datasetsLoading: false
});

const get = (state) => {
    return state.setIn(['projectLoading'], true);;
};

const getFail = (state) => {
    return state
        .setIn(['projectLoading'], false);
};

const getSuccess = (state, payload) => {
    return state
        .setIn(['project'], payload)
        .setIn(['projectLoading'], false);
};

const listDatasets = (state) => {
    return state.setIn(['datasetsLoading'], true);
};

const listDatasetsFail = (state) => {
    return state
        .setIn(['datasetsLoading'], false);
};

const listDatasetsSuccess = (state, payload) => {
    return state
        .setIn(['datasets'], _.get(payload, 'datasets', []))
        .setIn(['datasetsLoading'], false);
};

export default (state = initialState, action) => {
    switch (action.type) {
        case types.GET:
            return get(state, action.payload);
        case types.GET_FAIL:
            return getFail(state, action.payload);
        case types.GET_SUCCESS:
            return getSuccess(state, action.payload);
        case types.LIST_DATASETS:
            return listDatasets(state, action.payload);
        case types.LIST_DATASETS_FAIL:
            return listDatasetsFail(state, action.payload);
        case types.LIST_DATASETS_SUCCESS:
            return listDatasetsSuccess(state, action.payload);
        default:
            return state;
    }
};
