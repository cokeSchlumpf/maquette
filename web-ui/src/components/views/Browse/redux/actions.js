import constantsFromArray from '../../../../utils/constants-from-array';

export const types = constantsFromArray([
    'FOO',
    'LOREM'
], 'VIEWS_ASSETS');

export const foo = (payload) => (
    { type: types.FOO, payload }
);

export const lorem = (payload) => (
    { type: types.LOREM, payload }
);

export default {
    foo,
    lorem
}