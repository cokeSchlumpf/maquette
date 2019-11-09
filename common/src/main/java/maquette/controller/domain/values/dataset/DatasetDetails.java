package maquette.controller.domain.values.dataset;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import maquette.controller.domain.values.core.Markdown;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.governance.GovernanceProperties;
import maquette.controller.domain.values.exceptions.NoVersionException;
import maquette.controller.domain.values.exceptions.UnknownVersionException;
import maquette.controller.domain.values.iam.UserId;

@Value
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasetDetails {

    private static final String ACL = "acl";
    private static final String ACCESS_REQUESTS = "access-requests";
    private static final String CREATED = "created";
    private static final String CREATED_BY = "created-by";
    private static final String DATASET = "dataset";
    private static final String DESCRIPTION = "description";
    private static final String GOVERNANCE = "governance";
    private static final String MODIFIED = "modified";
    private static final String MODIFIED_BY = "modified-by";
    private static final String VERSIONS = "versions";

    @JsonProperty(DATASET)
    private final ResourcePath dataset;

    @JsonProperty(CREATED)
    private final Instant created;

    @JsonProperty(CREATED_BY)
    private final UserId createdBy;

    @JsonProperty(MODIFIED)
    private final Instant modified;

    @JsonProperty(MODIFIED_BY)
    private final UserId modifiedBy;

    @JsonProperty(VERSIONS)
    private final Set<VersionTagInfo> versions;

    @JsonProperty(ACL)
    private final DatasetACL acl;

    @JsonProperty(DESCRIPTION)
    private final Markdown description;

    @JsonProperty(GOVERNANCE)
    private final GovernanceProperties governance;

    @JsonProperty(ACCESS_REQUESTS)
    private final Map<UID, DatasetAccessRequest> accessRequests;

    @JsonCreator
    public static DatasetDetails apply(
        @JsonProperty(DATASET) ResourcePath dataset,
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(CREATED_BY) UserId createdBy,
        @JsonProperty(MODIFIED) Instant modified,
        @JsonProperty(MODIFIED_BY) UserId modifiedBy,
        @JsonProperty(VERSIONS) Set<VersionTagInfo> versions,
        @JsonProperty(ACL) DatasetACL acl,
        @JsonProperty(DESCRIPTION) Markdown description,
        @JsonProperty(GOVERNANCE) GovernanceProperties governance,
        @JsonProperty(ACCESS_REQUESTS) Map<UID, DatasetAccessRequest> accessRequests) {

        return new DatasetDetails(
            dataset, created, createdBy, modified, modifiedBy,
            ImmutableSet.copyOf(versions), acl, description, governance, ImmutableMap.copyOf(accessRequests));
    }

    @Deprecated
    public static DatasetDetails apply(
        ResourcePath dataset, Instant created, UserId createdBy, Instant modified,
        UserId modifiedBy, Set<VersionTagInfo> versions, DatasetACL acl) {

        return apply(
            dataset,
            created,
            createdBy,
            modified,
            modifiedBy,
            versions,
            acl,
            Markdown.apply(),
            GovernanceProperties.apply(),
            Maps.newHashMap());
    }

    public UID findVersionId(VersionTag tag) {
        return versions
            .stream()
            .filter(info -> info.getVersion().equals(tag))
            .map(VersionTagInfo::getId)
            .findAny()
            .orElseThrow(() -> UnknownVersionException.apply(tag));
    }

    public UID findLatestVersion() {
        return versions
            .stream()
            .max(Comparator.comparing(VersionTagInfo::getVersion))
            .map(VersionTagInfo::getId)
            .orElseThrow(NoVersionException::apply);
    }

    public Optional<Markdown> getDescription() {
        if (description == null || description.getValue().length() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(description);
        }
    }

    public DatasetDetails withAccessRequest(DatasetAccessRequest request) {
        Map<UID, DatasetAccessRequest> requests = Maps.newHashMap(accessRequests);
        requests.put(request.getId(), request);
        return withAccessRequests(ImmutableMap.copyOf(requests));
    }

    public DatasetDetails withVersion(VersionTagInfo withVersion) {
        Set<VersionTagInfo> versions$new = Sets.newHashSet(this.versions);
        versions$new.add(withVersion);
        return withVersions(versions$new);
    }

}
