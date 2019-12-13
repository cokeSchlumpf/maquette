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
        <tr key={ property }>
            <td>{ property }</td>
            <td>{ properties[property].value }</td>
        </tr>));

    return (
        <table className="mq-properties--table">
            <tbody>
                { entries }
            </tbody>
        </table>);
}