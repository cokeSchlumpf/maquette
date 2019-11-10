import _ from 'lodash';
import React, { useState } from 'react';
import './styles.scss';

import { CodeSnippet } from 'carbon-components-react';

const defaultViewModel = {
    'version': '1.0.0',
    'records': 42,
    'committed-by': 'bob',
    'committed-at': 'few moments ago',
    'message': 'lorem ipsum dolor sit amet',
    'schema': {
        "type": "record",
        "namespace": "com.example",
        "name": "FullName",
        "fields": [
            { "name": "first", "type": "string" },
            { "name": "last", "type": "string" }
        ],
        'foo': {
            'version': '1.0.0',
            'records': 42,
            'committed-by': 'bob',
            'committed-at': 'few moments ago',
            'message': 'lorem ipsum dolor sit amet',
            'schema': {
                "type": "record",
                "namespace": "com.example",
                "name": "FullName",
                "fields": [
                    { "name": "first", "type": "string" },
                    { "name": "last", "type": "string" }
                ]
            }
        }
    }
};

export default ({
    data = defaultViewModel
}) => {
    const version = _.assign({}, defaultViewModel, data);
    const schema = JSON.stringify(_.get(version, 'schema', {}), null, 2);
    const [schemaVisible, setSchemaVisible] = useState(false);

    const onShowSchema = () => {
        setSchemaVisible(!schemaVisible);
    };

    const onCopy = () => {
        navigator.clipboard.writeText(schema);
    };

    return (
        <div className="mq-datasetversion-card">
            <div className="mq-datasetversion-card--header">
                <h5 className="mq-datasetversion-card--name">
                    { version.version }
                    <span className="mq-datasetversion-card--more-button" onClick={ onShowSchema }>...</span>
                </h5>

                <p className="mq-datasetversion-card--description">
                    { version.message }
                </p>

                {
                    schemaVisible && (
                        <div className="mq-datasetversion-card--schema">
                            <h6 className="mq-datasetversion-card--schema-header">Schema</h6>

                            <CodeSnippet type="multi" onClick={ onCopy }>{ schema }</CodeSnippet>
                        </div>)
                }

                <ul className={"mq-datasetversion-card--details"}>
                    <li>{ _.get(version, 'records') } Records</li>
                    <li>{ _.get(version, 'committed-by') }</li>
                    <li>{ _.get(version, 'committed-at') }</li>
                </ul>
            </div>
        </div>);
}