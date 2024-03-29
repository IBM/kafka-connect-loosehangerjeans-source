package com.ibm.eventautomation.demos.loosehangerjeans.data;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

/**
 * Information about a country.
 */
public class Country {

    /** The two-letter country code */
    private final String code;

    /** The name of the country */
    private final String name;

    // TODO Documentation
    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("country")
            .version(1)
            .field("code",      Schema.STRING_SCHEMA)
            .field("name",      Schema.STRING_SCHEMA)
            .build();

    /** Creates a country object using the provided code and name. */
    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // TODO Documentation
    public Struct toStruct() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("code"),    code);
        struct.put(SCHEMA.field("name"),    name);
        return struct;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Country [code=" + code + ", name=" + name + "]";
    }
}
