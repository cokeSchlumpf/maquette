import { fromJS } from 'immutable';
import { types } from './actions';
import { types as serviceTypes } from '../../../redux/services/actions';

export const initialState = fromJS({
    notifications: 0,
    userPanelExpanded: false,
    user: {}
});

const clickUser = state => {
    return state.setIn(['userPanelExpanded'], !state.toJS().userPanelExpanded);
};

const updateUser = (state, payload) => {
    return state.setIn(['user'], payload);
};

export default (state = initialState, action) => {
    switch (action.type) {
        case types.CLICK_USER:
            return clickUser(state, action.payload);
        case serviceTypes.user.FETCH_SUCCESS:
            return updateUser(state, action.payload);
        default:
            return state;
    }
};