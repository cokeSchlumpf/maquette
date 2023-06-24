package maquette.common.functions;


import maquette.common.Operators;

@FunctionalInterface
public interface Function0<R> {

    R apply() throws Exception;

    default R get() {
        return Operators.suppressExceptions(this::apply);
    }

}
