package maquette.controller.domain.values.core;

import lombok.Value;

@Value
public class Ok {

    public static Ok INSTANCE = new Ok();

    private Ok() {

    }

}
