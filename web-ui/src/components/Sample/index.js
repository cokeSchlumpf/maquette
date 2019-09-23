import React, { Component } from 'react';
import { Container, Row, Col } from 'react-grid-system';

import {
    Form,
    FormItem,
    FormLabel,
    TextInput,
    Button
} from 'carbon-components-react';
import { Row16 } from '@carbon/icons-react';

class Sample extends Component {

    render() {
        return (
            <div>
                <p className="mq--spacing-05">Login to Maquette <span className="mq--type-semibold">Data Services</span></p>
                <Form>
                    <FormItem className="mq--spacing-05">
                        <FormLabel>Username</FormLabel>
                        <TextInput type="text" />
                    </FormItem>
                    <FormItem className="mq--spacing-05">
                        <FormLabel>Password</FormLabel>
                        <TextInput type="password" />
                    </FormItem>

                    <Button type="submit">Login</Button>
                </Form>
            </div>);
    }

}

export default Sample;