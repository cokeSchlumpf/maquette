export const types = {
    GET: 'PROJECT_GET',
    GET_SUCCESS: 'PROJECT_GET_SUCCESS',
    GET_FAIL: 'PROJECT_GET_FAIL',
    LIST_DATASETS: 'PROJECT_LIST_DATASETS',
    LIST_DATASETS_SUCCESS: 'PROJECT_LIST_DATASETS_SUCCESS',
    LIST_DATASETS_FAIL: 'PROJECT_LIST_DATASETS_FAIL'
};

export const get = (project) => (
    { type: types.GET, payload: { project } }
);

export const getFail = (error) => (
    { type: types.GET_FAIL, payload: { error } }
);

export const getSuccess = (project) => (
    { type: types.GET_SUCCESS, payload: project }
);

export const listDatasets = (project) => (
    { type: types.LIST_DATASETS, payload: { project } }
);

export const listDatasetsFail = (error) => (
    { type: types.LIST_DATASETS_FAIL, payload: { error } }
);

export const listDatasetsSuccess = (datasets) => (
    { type: types.LIST_DATASETS_SUCCESS, payload: datasets }
);

export default {
    get,
    getFail,
    getSuccess,
    listDatasets,
    listDatasetsFail,
    listDatasetsSuccess
}