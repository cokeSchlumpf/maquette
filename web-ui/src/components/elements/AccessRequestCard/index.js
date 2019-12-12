import _ from 'lodash';
import React, { useState } from 'react';
import './styles.scss';

import { Button } from 'carbon-components-react';

const defaultViewModel = {
  "canApprove": false,
  "canRevoke": false,
  "id": "a37d0df0",
  "initiatedBy": "hippo",
  "initiated": "2 weeks ago",
  "justification": "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
  "grant": "consumer",
  "grantFor": {
    "type": "user",
    "authorization": "hippo"
  },
  "approved": null,
  "revoked": null
};

export default ({
                    data = defaultViewModel
                }) => {
    const request = _.assign({}, defaultViewModel, data);

    return (
        <div className="mq-access-request-card">
            <div className="mq-access-request-card--header">
                <table className="mq-access-request-card--table">
                    <tbody>
                        <tr>
                            <td>Id</td>
                            <td>{ request.id }</td>
                        </tr>
                        <tr>
                            <td>Initiated by</td>
                            <td>{ request.initiatedBy }</td>
                        </tr>
                        <tr>
                            <td>Initiated</td>
                            <td>{ request.initiated }</td>
                        </tr>
                        <tr>
                            <td>Requested Grant</td>
                            <td>{ request.grant }</td>
                        </tr>
                        <tr>
                            <td>Requested for</td>
                            <td>{ _.get(request, "grantFor.authorization") }</td>
                        </tr>
                        <tr>
                            <td>Justification</td>
                            <td>{ defaultViewModel.justification }</td>
                        </tr>
                        <tr>
                            <td>Actions</td>
                            <td><Button kind="secondary" size="small">Revoke Request</Button></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>);
}