import _ from 'lodash';
import React from 'react';
import './styles.scss';

import { Link } from 'react-router-dom';

const defaultViewModel = {
  'name': 'name',
  'can-consume': true,
  'can-produce': true,
  'can-manage': true,
  'description': 'Lorem ipsum dolor sit amet',
  'versions': 0,
  'last-update': '2019-24-03 18:22'
};

export default ({
    data = defaultViewModel
}) => {
    data = _.assign({}, defaultViewModel, data);

    const access = _.join(_.filter(
        [
            data['can-consume'] && 'consume',
            data['can-produce'] && 'produce',
            data['can-manage'] && 'manage',
            !data['can-consume'] && !data['can-produce'] && !data['can-manage'] && 'no data access'],
        i => i),
        ' | ');

    return (
        <div className="mq--dataset-card">
            <h5 className="mq--dataset-card--name"><Link to={ "/datasets/" + data.name }>{ data.name }</Link></h5>

            {
                (data.description && <p className="mq--dataset-card--description">{ data.description }</p>) ||
                <p className={"mq--dataset-card--no-description"}>No description</p>
            }
            <ul className={"mq--dataset-card--details"}>
                <li>{ access }</li>
                <li>{ data.versions } version(s)</li>
                <li>{ data['last-update'] }</li>
            </ul>
        </div>);
}