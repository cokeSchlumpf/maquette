import _ from 'lodash';

import Component from './component';
import actions from './redux/actions';
import { connect } from 'react-redux';

const mapStateToProps = (state) => {
    const s = state.toJS();
    return _.assign({}, s.services.dataset, s.services.project);
};

const mapDispatchToProps = (dispatch) => {
    return {
        onInit: (project) => dispatch(actions.init(project))
    };
};

const VisibleComponent = connect(
    mapStateToProps,
    mapDispatchToProps
)(Component);

export default VisibleComponent;
