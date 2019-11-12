import React from 'react';
import './styles.scss';

import ContentSection from '../ContentSection';
import { Form, FormGroup, RadioButtonGroup, RadioButton, TextArea } from 'carbon-components-react';

export default () => {
    return (
        <>
            <h3 className="mq--cards-heading">Request Access</h3>
            <Form>
                <ContentSection>
                    <FormGroup legendText="Select the required access">
                        <RadioButtonGroup
                            name="authorization"
                            id="authorization"
                            defaultSelected="consumer">

                            <RadioButton value="consumer" id="consumer" labelText="consumer" />
                            <RadioButton value="producer" id="producer" labelText="producer" />
                            <RadioButton value="member" id="member" labelText="member" />
                        </RadioButtonGroup>
                    </FormGroup>

                    <TextArea labelText="Justification" helperText="Enter a justification for your request" />
                </ContentSection>
            </Form>
        </>);
}