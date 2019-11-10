import _ from 'lodash';

import Component from './component';
import { connect } from 'react-redux';

const mapStateToProps = (state) => {
    const s = state.toJS();
    return _.assign({}, s.services.project);
};

const mapDispatchToProps = (dispatch) => {
    return { };
};

const VisibleComponent = connect(
    mapStateToProps,
    mapDispatchToProps
)(Component);

export default VisibleComponent;
