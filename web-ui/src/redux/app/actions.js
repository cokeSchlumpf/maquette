import constantsFromArray from '../../utils/constants-from-array';

export const types = constantsFromArray([
    'FETCH_SETTINGS',
    'FETCH_SETTINGS_SUCCESS',
    'FETCH_SETTINGS_FAIL',
    'INIT'
], 'APP_');

export const fetchSettings = () => (
    { type: types.FETCH_SETTINGS, payload: { } }
);

export const fetchSettingsFail = (error) => (
    { type: types.FETCH_SETTINGS_FAIL, payload: { error } }
);

export const fetchSettingsSuccess = (payload) => (
    { type: types.FETCH_SETTINGS_SUCCESS, payload }
);

export const init = () => (
    { type: types.INIT, payload: { } }
);

export default {
    fetchSettings,
    fetchSettingsFail,
    fetchSettingsSuccess,
    init
}