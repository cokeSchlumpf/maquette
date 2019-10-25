import React from 'react'
import Shell from '../../elements/Shell'

export default (props) => {
    const {children, ...otherProps} = props;

    return (<Shell { ...otherProps}>{ children }</Shell>);
}
