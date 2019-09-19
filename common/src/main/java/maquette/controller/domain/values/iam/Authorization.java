package maquette.controller.domain.values.iam;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
                  @JsonSubTypes.Type(value = RoleAuthorization.class, name = "role"),
                  @JsonSubTypes.Type(value = TokenAuthorization.class, name = "token"),
                  @JsonSubTypes.Type(value = UserAuthorization.class, name = "user"),
                  @JsonSubTypes.Type(value = WildcardAuthorization.class, name = "wildcard")
              })
public interface Authorization {

    @JsonIgnore
    boolean hasAuthorization(User user);

}
