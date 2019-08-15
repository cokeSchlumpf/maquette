package maquette.controller.domain.services;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class CreateDatasetService {

    private static Behavior<Object> apply() {
        return Behaviors
            .receive(Object.class)
            .onAnyMessage((ctx, obj) -> {
                // ctx.messageAdapter()
                return Behaviors.same();
            })
            .build();
    }

}
