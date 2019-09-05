package maquette.controller.domain.values.dataset;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.exceptions.InvalidVersionException;

@Value
@AllArgsConstructor(staticName = "apply")
@JsonSerialize(using = VersionTag.Serializer.class)
@JsonDeserialize(using = VersionTag.Deserializer.class)
public class VersionTag implements Comparable<VersionTag> {

    private static final Pattern PATCH_VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)$");
    private static final Pattern MINOR_VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)$");
    private static final Pattern MAJOR_VERSION_PATTERN = Pattern.compile("(\\d+)$");

    private final int major;

    private final int minor;

    private final int patch;

    public static VersionTag apply(String s) {
        final Matcher patchMatcher = PATCH_VERSION_PATTERN.matcher(s);
        final Matcher minorMatcher = MINOR_VERSION_PATTERN.matcher(s);
        final Matcher majorMatcher = MAJOR_VERSION_PATTERN.matcher(s);

        if (patchMatcher.find()) {
            int major = Integer.valueOf(patchMatcher.group(1));
            int minor = Integer.valueOf(patchMatcher.group(2));
            int patch = Integer.valueOf(patchMatcher.group(3));

            return apply(major, minor, patch);
        } else if (minorMatcher.find()) {
            int major = Integer.valueOf(minorMatcher.group(1));
            int minor = Integer.valueOf(minorMatcher.group(2));

            return apply(major, minor, 0);
        } else if (majorMatcher.find()) {
            int major = Integer.valueOf(majorMatcher.group(1));

            return apply(major, 0, 0);
        } else {
            throw InvalidVersionException.apply(s);
        }
    }

    @Override
    public int compareTo(VersionTag o) {
        int major = Integer.compare(getMajor(), o.major);
        int minor = Integer.compare(getMinor(), o.minor);
        int patch = Integer.compare(getPatch(), o.patch);

        if (major == 0 && minor == 0) {
            return patch;
        } else if (major == 0) {
            return minor;
        } else {
            return major;
        }
    }

    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

    public static class Serializer extends StdSerializer<VersionTag> {

        private Serializer() {
            super(VersionTag.class);
        }

        @Override
        public void serialize(VersionTag value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toString());
        }

    }

    public static class Deserializer extends StdDeserializer<VersionTag> {

        private Deserializer() {
            super(VersionTag.class);
        }

        @Override
        public VersionTag deserialize(JsonParser p, DeserializationContext ignore) throws IOException {
            return VersionTag.apply(p.readValueAs(String.class));
        }

    }

}
