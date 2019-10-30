import _ from 'lodash';
import Component from './component';
import actions from './redux/actions';
import { connect } from 'react-redux';

const mapStateToProps = (state) => {
    const s = state.toJS();
    return _.assign(
        s.components.app,
        s.app,
        { user: _.get(s, 'services.user', {}) });
};

const mapDispatchToProps = (dispatch) => {
    return {
        onClickUser: () => dispatch(actions.clickUser())
    };
};

const VisibleComponent = connect(
    mapStateToProps,
    mapDispatchToProps
)(Component);

export default VisibleComponent;
