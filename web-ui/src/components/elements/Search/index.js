import _ from 'lodash';
import React, { useState } from 'react';

import { Search } from 'carbon-components-react';

export default (props) => {
    const [value, setValue] = useState(_.get(props, 'value', ''));
    const [timeout, updateTimeout] = useState();

    const onChangeValue = (event) => {
        timeout && clearTimeout(timeout);

        const v = event.target.value;
        setValue(v);

        if (props.onChange) {
            updateTimeout(setTimeout(() => props.onChange(v), 500));
        }
    };

    return <Search { ...props } labelText="Search" value={ value } onChange={ onChangeValue } />;
}