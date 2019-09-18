package maquette.sdk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PublishDatasetVersionRequest {

    private final String message;

    @JsonCreator
    public static PublishDatasetVersionRequest apply(
        @JsonProperty("message") String message) {

        return new PublishDatasetVersionRequest(message);
    }

}
