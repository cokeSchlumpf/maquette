package maquette.controller.domain.api.commands.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.commands.EAuthorizationType;
import maquette.controller.domain.values.iam.Authorization;
import maquette.controller.domain.values.iam.GrantedAuthorization;
import maquette.controller.domain.values.iam.RoleAuthorization;
import maquette.controller.domain.values.iam.UserAuthorization;
import maquette.controller.domain.values.iam.WildcardAuthorization;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationVM {

    private static final String TYPE = "type";
    private static final String AUTHORIZATION = "authorization";

    @JsonProperty(TYPE)
    private final EAuthorizationType type;

    @JsonProperty(AUTHORIZATION)
    private final String authorization;

    @JsonCreator
    public static AuthorizationVM apply(
        @JsonProperty(TYPE) EAuthorizationType type,
        @JsonProperty(AUTHORIZATION) String authorization) {

        return new AuthorizationVM(type, authorization);
    }

    public static AuthorizationVM apply(Authorization authorization) {
        if (authorization instanceof UserAuthorization) {
            return apply(EAuthorizationType.USER, ((UserAuthorization) authorization).getName());
        } else if (authorization instanceof RoleAuthorization) {
            return apply(EAuthorizationType.ROLE, ((RoleAuthorization) authorization).getName());
        } else if (authorization instanceof WildcardAuthorization) {
            return apply(EAuthorizationType.WILDCARD, null);
        } else {
            throw new IllegalArgumentException("Unknown authorization type");
        }
    }

    public static AuthorizationVM apply(GrantedAuthorization authorization) {
        return apply(authorization.getAuthorization());
    }

}
