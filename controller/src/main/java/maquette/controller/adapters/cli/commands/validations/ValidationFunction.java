package maquette.controller.adapters.cli.commands.validations;

@FunctionalInterface
public interface ValidationFunction<T> {

    void validate(T object, String field);

    default ValidationFunction<T> and(ValidationFunction<? super T> other) {
        return (object, field) -> {
            this.validate(object, field);
            other.validate(object, field);
        };
    }

}
