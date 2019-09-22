import { fromJS } from 'immutable';

export const initialState = fromJS({

});

export default (state = initialState, action) => {
    switch (action.type) {
        default:
            return state;
    }
};
