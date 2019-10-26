import _ from 'lodash';

import Component from './component';
import actions from './redux/actions';
import { connect } from 'react-redux';

const mapStateToProps = (state) => {
    const s = state.toJS();
    return _.assign({
        projects: s.services.projects.projects
    });
};

const mapDispatchToProps = (dispatch) => {
    return {
        // onUserMessageChange: (message) => dispatch(actions.userMessageChange(message)),
        // onUserMessageSubmit: (message) => dispatch(actions.userMessageSubmit(message))
    };
};

const VisibleComponent = connect(
    mapStateToProps,
    mapDispatchToProps
)(Component);

export default VisibleComponent;
