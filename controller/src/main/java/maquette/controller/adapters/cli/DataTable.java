package maquette.controller.adapters.cli;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = DataTable.Serializer.class)
@JsonDeserialize(using = DataTable.Deserializer.class)
public class DataTable {

    private final List<String> headers;

    private final List<List<String>> rows;

    private static DataTable apply(List<String> headers, List<List<String>> rows) {
        return new DataTable(
            ImmutableList.copyOf(headers.stream().map(String::toUpperCase).collect(Collectors.toList())),
            ImmutableList.copyOf(rows.stream().map(ImmutableList::copyOf).collect(Collectors.toList())));
    }

    public static DataTable apply(List<String> headers) {
        return DataTable.apply(headers, Lists.newArrayList());
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
            result = result.withRow(line.split(";"));
        }

        return result;
    }

    public DataTable withRow(List<String> row) {
        if (row.size() != headers.size()) {
            throw new IllegalArgumentException("row must contain same number of columns as headers of table");
        }

        if (String.join("", row).contains(";")) {
            throw new IllegalArgumentException("field cannot contain semicolon (;)");
        }

        List<List<String>> rows$new = Lists.newArrayList(rows);
        rows$new.add(row);

        return apply(headers, rows$new);
    }

    public DataTable withRow(String ...columns) {
        return withRow(Lists.newArrayList(columns));
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
