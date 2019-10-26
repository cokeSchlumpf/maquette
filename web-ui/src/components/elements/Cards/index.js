import _ from 'lodash';
import React, { useEffect, useState } from 'react';
import './styles.scss';

import { Button } from 'carbon-components-react';

export default ({ component, cards = [], title = "Items", pageSize = 3 }) => {
    const itemsCount = _.size(cards);
    const [items, setItems] = useState(0);

    const Component = component;
    const visibleCards = _.take(
        _.map(cards, (card, i) => <Component data={ card } key={ "card-" + i } />),
        items);

    const onButtonClick = () => {
      setItems(_.min([items + pageSize, itemsCount]));
    };

    useEffect(() => {
        setItems(_.min([itemsCount, pageSize]))
    }, [itemsCount, pageSize]);

    return (
        <>
            <h3 className="mq--cards-heading">{ title } ({items} of {itemsCount})</h3>
            { (itemsCount > 0 &&
                <>
                    { visibleCards }
                    <div className="mq--cards-button">
                        <Button size="small" disabled={ items >= itemsCount} onClick={ onButtonClick }>Show more</Button>
                </div>
                </>) || <p className="mq--cards-no-items">No items to show</p> }
        </>);
}