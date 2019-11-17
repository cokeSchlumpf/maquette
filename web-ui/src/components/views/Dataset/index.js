import _ from 'lodash';

import Component from './component';
import actions from './redux/actions';
import { connect } from 'react-redux';

const mapStateToProps = (state) => {
    const s = state.toJS();
    return _.assign(
        {},
        _.get(s, 'services.dataset'),
        _.get(s, 'services.project'),
        { user: _.get(s, 'services.user') });
};

const mapDispatchToProps = (dispatch) => {
    return {
        onSubmitAccessRequest: (request) => dispatch(actions.createAccessRequest(request))
    };
};

const VisibleComponent = connect(
    mapStateToProps,
    mapDispatchToProps
)(Component);

export default VisibleComponent;
