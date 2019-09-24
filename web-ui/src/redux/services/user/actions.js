import constantsFromArray from '../../../utils/constants-from-array';

export const types = constantsFromArray([
    'FETCH',
    'FETCH_FAIL',
    'FETCH_SUCCESS'
], 'SERVICES_USER_');

export const fetch = () => (
    { type: types.FETCH, payload: {} }
);

export const fetchFail = (error) => (
    { type: types.FETCH_FAIL, payload: { error } }
);

export const fetchSuccess = (payload) => (
    { type: types.FETCH_SUCCESS, payload }
);

export default {
    fetch,
    fetchFail,
    fetchSuccess
}