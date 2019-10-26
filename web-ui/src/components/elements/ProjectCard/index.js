import _ from 'lodash';
import React from 'react';
import './styles.scss';

import { Link } from 'react-router-dom';

const defaultViewModel = {
  'name': 'name',
  'description': 'Lorem ipsum dolor sit amet',
  'datasets': 0,
  'last-update': '2019-24-03 18:22'
};

export default ({
    data = defaultViewModel
}) => {
    data = _.assign({}, defaultViewModel, data);

    return (
        <div className="mq--project-card">
            <h5 className="mq--project-card--name"><Link to="/projects/foo/bar">{ data.name }</Link></h5>

            {
                (data.description && <p className="mq--project-card--description">{ data.description }</p>) ||
                <p className={"mq--project-card--no-description"}>No description</p>
            }
            <ul className={"mq--project-card--details"}>
                <li>{ data.datasets } Datasets</li>
                <li>Last modified { _.get(data, 'last-update') }</li>
            </ul>
        </div>);
}