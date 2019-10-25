import React from 'react';
import './styles.scss';

export default ({children}) => {
    return <div className="mq--content-container">{ children }</div>;
}