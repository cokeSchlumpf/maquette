import { fromJS } from 'immutable';

export const initialState = fromJS({
    value: "",
    output: "Enter your name and press submit!"
});

export default (state = initialState, action) => {
    switch (action.type) {
        default:
            return state;
    }
};