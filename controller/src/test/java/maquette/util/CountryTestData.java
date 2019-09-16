package maquette.util;

import java.nio.file.Paths;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import maquette.controller.domain.values.core.records.Records;

public final class CountryTestData {

    private static String NAME = "name";
    private static String CAPITAL = "capital";
    private static String CITIZENS = "citizens";

    private CountryTestData() {

    }

    public static Schema getSchema() {
        String COUNTRY = "country";

        return SchemaBuilder
            .record(COUNTRY)
            .fields()
            .requiredString(NAME)
            .requiredString(CAPITAL)
            .requiredInt(CITIZENS)
            .endRecord();
    }

    public static List<GenericData.Record> getRecords() {
        List<GenericData.Record> records = Lists.newArrayList();
        records.add(createRecord("Germany", "Berlin", 80000000));
        records.add(createRecord("Switzerland", "Berne", 16000000));
        records.add(createRecord("Austria", "Vienna", 16000000));
        records.add(createRecord("France", "Paris", 60000000));
        records.add(createRecord("Italy", "Rome", 40000000));
        return ImmutableList.copyOf(records);
    }

    private static GenericData.Record createRecord(String name, String capital, int citizens) {
        return new GenericRecordBuilder(getSchema())
            .set(NAME, name)
            .set(CAPITAL, capital)
            .set(CITIZENS, citizens)
            .build();
    }

}
