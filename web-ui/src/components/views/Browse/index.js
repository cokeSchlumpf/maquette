import _ from 'lodash';

import Component from './component';
import actions from './redux/actions';
import { connect } from 'react-redux';

const mapStateToProps = (state) => {
    const s = state.toJS();
    return _.assign({
        datasetsLoading: s.services.datasets.datasetsLoading,
        datasets: s.services.datasets.datasets,
        projectsLoading: s.services.projects.projectsLoading,
        projects: s.services.projects.projects
    });
};

const mapDispatchToProps = (dispatch) => {
    return {
        onClearSearch: () => dispatch(actions.clearSearch()),
        onSearch: (query) => dispatch(actions.search(query))
    };
};

const VisibleComponent = connect(
    mapStateToProps,
    mapDispatchToProps
)(Component);

export default VisibleComponent;
