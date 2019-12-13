import _ from 'lodash';
import React, { useState } from 'react';
import './styles.scss';

import { Button } from 'carbon-components-react';

import Properties from '../Properties';

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
    const propertyValues = {
        "Id": request.id,
        "Initiated by": request.initiatedBy,
        "Initiated": request.initiated,
        "Requested Grant": request.grant,
        "Requested for": _.get(request, "grantFor.authorization"),
        "Justification": defaultViewModel.justification,
        "Actions": (<Button kind="secondary" size="small">Revoke Request</Button>)
    };

    const properties = _.mapValues(propertyValues, value => ({ value: value }));

    return (
        <div className="mq-access-request-card">
            <div className="mq-access-request-card--header">
                <Properties properties={ properties } />
            </div>
        </div>);
}