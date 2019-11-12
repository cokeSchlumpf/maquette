import _ from 'lodash';
import React, { useState } from 'react';
import './styles.scss';

import ContentSection from '../ContentSection';
import { Button, Form, FormGroup, RadioButtonGroup, RadioButton, TextArea } from 'carbon-components-react';

const onSubmitDefault = (value) => {
    console.log(value);
};

export default ({ onSubmit = onSubmitDefault }) => {

    const defaultValue = { authorization: "consumer", justification: "" };
    const [value, setValue] = useState(defaultValue);
    const [justificationInvalid, setJustificationInvalid] = useState(false);

    const onChange = (field, fieldValue) => {
        setValue(_.assign(value, { [field]: fieldValue }))
    };

    const onChangeInput = (field) => (event) => {
        onChange(field, event.target.value);
    };

    const onChangeRadioGroup = (value, field, event) => {
        onChange(field, value);
    };

    const onSubmitButtonClick = (event) => {
        event.preventDefault();

        if (value.justification.length === 0) {
            setJustificationInvalid(true);
        } else {
            onSubmit(value);
        }
    };

    return (
        <>
            <h3 className="mq--cards-heading">Request Access</h3>
            <Form>
                <ContentSection>
                    <FormGroup legendText="Select the required access">
                        <RadioButtonGroup
                            name="authorization"
                            id="authorization"
                            defaultSelected={ defaultValue.authorization }
                            onChange={ onChangeRadioGroup }>

                            <RadioButton value="consumer" id="consumer" labelText="consumer" />
                            <RadioButton value="producer" id="producer" labelText="producer" />
                            <RadioButton value="member" id="member" labelText="member" />
                        </RadioButtonGroup>
                    </FormGroup>

                    <FormGroup legendText="">
                        <TextArea
                            labelText="Justification"
                            helperText="Enter a justification for your request"
                            defaultValue={ defaultValue.justification }
                            invalid={ justificationInvalid }
                            invalidText="Justification must not be empty!"
                            onChange={ onChangeInput("justification") } />
                    </FormGroup>

                    <Button type="submit" onClick={ onSubmitButtonClick }>Submit Request</Button>
                </ContentSection>
            </Form>
        </>);
}