export const types = {
    INIT: 'PROJECT_VIEW_INIT'
};

export const init = (project) =>(
    { type: types.INIT, payload: { project } }
);

export default {
    init
}