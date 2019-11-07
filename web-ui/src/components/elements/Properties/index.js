import _ from 'lodash';
import React from 'react';
import './styles.scss';

const defaultProperties = {
    "Owner": {
        value: "completed",
        copy: false
    },
    "Private": {
        value: "yes",
        copy: false
    },
    "Lorem Ipsum": {
        value: "foo bbaaar",
        copy: true
    }
};

export default ({ properties = defaultProperties }) => {
    const entries = _.map(_.keys(properties), property => (
        <div className="mq--properties--property" key={ property }>
            <div className="mq--properties--label">{ property }</div>
            <div className="mq--properties--value">{ properties[property].value }</div>
        </div>
    ));

    return <div className="mq--properties">{ entries }</div>;
}