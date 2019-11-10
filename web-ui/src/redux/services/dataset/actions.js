export const types = {
    GET: 'DATASET_GET',
    GET_SUCCESS: 'DATASET_GET_SUCCESS',
    GET_FAIL: 'DATASET_GET_FAIL',
    LIST_VERSIONS: 'DATASET_LIST_VERSIONS',
    LIST_VERSIONS_SUCCESS: 'DATASET_LIST_VERSIONS_SUCCESS',
    LIST_VERSIONS_FAIL: 'DATASET_LIST_VERSIONS_FAIL'
};

export const get = (project, dataset) => (
    { type: types.GET, payload: { project, dataset } }
);

export const getFail = (error) => (
    { type: types.GET_FAIL, payload: { error } }
);

export const getSuccess = (dataset) => (
    { type: types.GET_SUCCESS, payload: dataset }
);

export const listVersions = (project, dataset) => (
    { type: types.LIST_VERSIONS, payload: { project, dataset } }
);

export const listVersionsFail = (error) => (
    { type: types.LIST_VERSIONS_FAIL, payload: { error } }
);

export const listVersionsSuccess = (versions) => (
    { type: types.LIST_VERSIONS_SUCCESS, payload: versions }
);

export default {
    get,
    getFail,
    getSuccess,
    listVersions,
    listVersionsFail,
    listVersionsSuccess
}