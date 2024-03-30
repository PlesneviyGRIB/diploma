package com.savchenko.sqlTool.utils;

import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.savchenko.sqlTool.model.command.Where;

public class JsonSchemeGenerator {

    public static void main(String[] args) {

        var config = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON).build();

        var generator = new SchemaGenerator(config);
        var jsonSchema = generator.generateSchema(Where.class);

        System.out.println(jsonSchema);

    }

}
