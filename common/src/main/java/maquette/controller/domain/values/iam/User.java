package maquette.controller.domain.values.iam;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
                  @JsonSubTypes.Type(value = AnonymousUser.class, name = "anonymous"),
                  @JsonSubTypes.Type(value = AuthenticatedUser.class, name = "user"),
                  @JsonSubTypes.Type(value = TokenAuthenticatedUser.class, name = "token")
              })
public interface User {

    UserId getUserId();

    String getDisplayName();

    Set<String> getRoles();

    boolean isAdministrator();

    boolean isAuditor();

}
