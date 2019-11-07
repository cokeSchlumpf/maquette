import React from 'react';
import './styles.scss';

export default ({title, children, cols = false, rows = false}) => {

    const content = () => {
        if (rows) {
            return children;
        } else if (cols) {
            return (
                <div className="bx--row">
                    { children }
                </div>);
        } else {
            return (
                <div className="bx--row">
                    <div className="bx--col">
                        { children }
                    </div>
                </div>
            )
        }
    };

    return (
        <div className="bx--grid bx--no-gutter mq--content-section">
            <div className="bx--row">
                <div className="bx--col">
                    <h3 className="mq--content-section--header">{ title }</h3>
                </div>
            </div>
            { content() }
        </div>);
}