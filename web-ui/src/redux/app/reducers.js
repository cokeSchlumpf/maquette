import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({

});

const fetchSettingsSuccess = (state, payload) => {
    return fromJS(payload);
};

export default (state = initialState, action) => {
    switch (action.type) {
        case types.FETCH_SETTINGS_SUCCESS:
            return fetchSettingsSuccess(state, action.payload);
        default:
            return state;
    }
};
