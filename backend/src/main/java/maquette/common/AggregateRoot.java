package maquette.common;

import org.springframework.data.domain.AbstractAggregateRoot;

public abstract class AggregateRoot<T, SELF extends AggregateRoot<T, SELF>> extends AbstractAggregateRoot<SELF> {

    public abstract T getId();

}
