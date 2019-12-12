package maquette.controller.domain.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(exclude = { "outputFormat" })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = DataTable.Serializer.class)
@JsonDeserialize(using = DataTable.Deserializer.class)
public class DataTable {

    private final List<String> headers;

    private final List<List<String>> rows;

    private final OutputFormat outputFormat;

    private static DataTable apply(List<String> headers, List<List<String>> rows, OutputFormat outputFormat) {
        return new DataTable(
            ImmutableList.copyOf(headers.stream().map(String::toUpperCase).collect(Collectors.toList())),
            ImmutableList.copyOf(rows.stream().map(ImmutableList::copyOf).collect(Collectors.toList())),
            outputFormat);
    }

    public static DataTable apply(List<String> headers) {
        return apply(headers, OutputFormat.apply());
    }

    public static DataTable apply(List<String> headers, OutputFormat outputFormat) {
        return DataTable.apply(headers, Lists.newArrayList(), outputFormat);
    }

    public static DataTable apply(String ...headers) {
        return DataTable.apply(Lists.newArrayList(headers));
    }

    public static DataTable fromCSV(String csv) {
        List<String> lines = Lists.newArrayList(csv.split("\n"));
        if (lines.size() < 1) {
            throw new IllegalArgumentException("the csv table must have at least one line (the header)");
        }

        DataTable result = DataTable.apply(lines.get(0).split(";"));
        lines.remove(0);

        for (String line : lines) {
            String[] split = line.split(";");
            result = result.withRowFromList(Lists.newArrayList((Object[]) split));
        }

        return result;
    }

    public DataTable withRowFromList(List<Object> row) {
        if (row.size() != headers.size()) {
            throw new IllegalArgumentException("row must contain same number of columns as headers of table");
        }

        List<String> row$new = row
            .stream()
            .map(outputFormat::format)
            .collect(Collectors.toList());

        if (String.join("", row$new).contains(";")) {
            throw new IllegalArgumentException("field cannot contain semicolon (;)");
        }

        List<List<String>> rows$new = Lists.newArrayList(rows);
        rows$new.add(row$new);

        return apply(headers, rows$new, outputFormat);
    }

    public DataTable withRow(Object ...columns) {
        return withRowFromList(Lists.newArrayList(columns));
    }

    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(";", headers));
        sb.append("\n");

        for (List<String> row : rows) {
            sb.append(String.join(";", row));
            sb.append("\n");
        }

        return sb.toString();
    }

    public String toAscii() {
        return toAscii(true, false);
    }

    public String toAscii(boolean printHeader, boolean rowHeader) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, Integer> widths = Maps.newHashMap();
        int spacing = 4;

        // calculate column widths
        for (int col = 0; col < headers.size(); col++) {
            widths.put(col, headers.get(col).length());
        }

        for (List<String> row : rows) {
            for (int col = 0; col < row.size(); col++) {
                String content = row.get(col);
                int currentMaxWidth = widths.get(col) != null ? widths.get(col) : 0;

                if (content != null && (content.length() > currentMaxWidth)) {
                    widths.put(col, content.length());
                }
            }
        }

        // Print the header
        if (printHeader) {
            for (int col = 0; col < headers.size(); col++) {
                String title = headers.get(col);
                int width = widths.get(col) != null ? widths.get(col) : title.length();
                String spaces = StringUtils.repeat(" ", width - title.length() + spacing);

                sb.append(String.format("%s%s", title.toUpperCase(), spaces));
            }

            sb.append("\n");
        }

        // Print rows
        for (List<String> row : rows) {
            for (int col = 0; col < row.size(); col++) {
                String content = row.get(col);

                if (rowHeader && col == 0) {
                    content = content.toUpperCase();
                }

                int contentLength = content == null ? 0 : content.length();

                int width = widths.get(col) != null ? widths.get(col) : contentLength;
                String spaces = StringUtils.repeat(" ", width - contentLength + spacing);
                sb.append(String.format("%s%s", content, spaces));
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public static class Serializer extends StdSerializer<DataTable> {

        protected Serializer() {
            super(DataTable.class);
        }

        @Override
        public void serialize(DataTable value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toCSV());
        }

    }

    public static class Deserializer extends StdDeserializer<DataTable> {

        protected Deserializer() {
            super(DataTable.class);
        }

        @Override
        public DataTable deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String csv = p.readValueAs(String.class);
            return DataTable.fromCSV(csv);
        }

    }

}
