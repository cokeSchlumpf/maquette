import _ from 'lodash';
import Component from './component';
import actions from './redux/actions';
import { connect } from 'react-redux';

const mapStateToProps = (state) => {
    const s = state.toJS();
    return _.assign(
        s.components.app,
        s.app,
        { foo: 'bar' });
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
