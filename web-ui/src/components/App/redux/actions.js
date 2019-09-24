import constantsFromArray from '../../../utils/constants-from-array';

export const types = constantsFromArray([
    'CLICK_USER'
], 'COMPONENTS_APP');

export const clickUser = () => (
    { type: types.CLICK_USER, payload: {} }
);

export default {
    clickUser
}