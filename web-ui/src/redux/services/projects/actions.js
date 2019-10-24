import constantsFromArray from '../../../utils/constants-from-array';

export const types = constantsFromArray([
    'FIND',
    'FIND_SUCCESS',
    'FIND_FAIL',
    'LIST',
    'LIST_SUCCESS',
    'LIST_FAIL'
], 'PROJECTS_');

export const find = (query) => (
    { type: types.FIND, payload: { query } }
);

export const findFail = (error) => (
    { type: types.FIND_FAIL, payload: { error } }
);

export const findSuccess = (payload) => (
    { type: types.FIND_SUCCESS, payload }
);

export const list = () => (
    { type: types.LIST, payload: {} }
);

export const listFail = (error) => (
    { type: types.LIST_FAIL, payload: { error } }
);

export const listSuccess = (payload) => (
    { type: types.LIST_SUCCESS, payload }
);

export default {
    find,
    findFail,
    findSuccess,
    list,
    listFail,
    listSuccess
}