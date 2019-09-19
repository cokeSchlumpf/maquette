package maquette.controller.domain.values.iam;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class WildcardAuthorization implements Authorization {

    @JsonCreator
    public static WildcardAuthorization apply() {
        return new WildcardAuthorization();
    }

    @Override
    @JsonIgnore
    public boolean hasAuthorization(User user) {
        return true;
    }

    @Override
    public String toString() {
        return "*";
    }

}
