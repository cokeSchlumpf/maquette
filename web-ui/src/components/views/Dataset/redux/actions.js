export const types = {
    CREATE_ACCESS_REQUEST: 'PROJECT_CREATE_ACCESS_REQUEST',
    CREATE_ACCESS_REQUEST_FAIL: 'PROJECT_CREATE_ACCESS_REQUEST_FAIL',
    CREATE_ACCESS_REQUEST_SUCCESS: 'PROJECT_CREATE_ACCESS_REQUEST_SUCCESS',
    INIT: 'PROJECT_VIEW_INIT'
};

export const createAccessRequest = (request) => (
    { type: types.CREATE_ACCESS_REQUEST, payload: { request } }
);

export const createAccessRequestFail = (error) => (
    { type: types.CREATE_ACCESS_REQUEST_FAIL, payload: { error } }
);

export const createAccessRequestSuccess = (request) => (
    { type: types.CREATE_ACCESS_REQUEST_SUCCESS, payload: { request } }
);

export const init = (project) =>(
    { type: types.INIT, payload: { project } }
);

export default {
    createAccessRequest,
    createAccessRequestFail,
    createAccessRequestSuccess,
    init
}