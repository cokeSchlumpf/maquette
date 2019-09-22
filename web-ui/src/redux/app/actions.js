import constantsFromArray from '../../utils/constants-from-array';

export const types = constantsFromArray([
    'INIT'
], 'APP_');

export const init = () => (
    { type: types.INIT, payload: { } }
);

export default {
    init
}