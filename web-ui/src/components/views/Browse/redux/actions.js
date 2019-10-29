import constantsFromArray from '../../../../utils/constants-from-array';

export const types = constantsFromArray([
    'CLEAR_SEARCH',
    'SEARCH'
], 'BROWSE');

export const clearSearch = () => (
    { type: types.CLEAR_SEARCH, payload: {} }
);

export const search = (query) => (
    { type: types.SEARCH, payload: { query } }
);

export default {
    clearSearch,
    search
}