package maquette.controller.domain.api.validations;

import java.util.Objects;

import maquette.controller.domain.api.EAuthorizationType;

public final class ObjectValidation<T> {

    public static <T> ValidationFunction<T> notNull() {
        return (obj, field) -> {
            if (Objects.isNull(obj)) {
                throw ValidationException.apply("'%s' must be set", field);
            }
        };
    }

    public static ValidationFunction<EAuthorizationType> validAuthorization(String to) {
        return (obj, field) -> {
            if (!Objects.isNull(obj) && obj.equals(EAuthorizationType.ROLE) && Objects.isNull(to)) {
                throw ValidationException.apply("'to'  must be specified for role authorization");
            } else if (!Objects.isNull(obj) && obj.equals(EAuthorizationType.USER) && Objects.isNull(to)) {
                throw ValidationException.apply("'to'  must be specified for user authorization");
            }
        };
    }

}
