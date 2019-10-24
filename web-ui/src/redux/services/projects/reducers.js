import { fromJS } from 'immutable';
import { types } from './actions';

export const initialState = fromJS({
    value: "",
    output: "Enter your name and press submit!"
});

const foo = state => {
    return state;
};

export default (state = initialState, action) => {
    switch (action.type) {
        case types.FOO:
            return foo(state, action.payload);
        default:
            return state;
    }
};