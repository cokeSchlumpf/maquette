package maquette.controller.domain.entities.dataset.protocol;

import java.time.Instant;

import org.codehaus.jackson.annotate.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.iam.UserId;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatedDataset {

    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String PATH = "path";

    @JsonProperty(PATH)
    private final ResourcePath path;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(CREATED)
    private final Instant created;

    public static CreatedDataset apply(
        @JsonProperty(PATH) ResourcePath path,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(CREATED) Instant created) {

        return new CreatedDataset(path, createdBy, created);
    }

}
