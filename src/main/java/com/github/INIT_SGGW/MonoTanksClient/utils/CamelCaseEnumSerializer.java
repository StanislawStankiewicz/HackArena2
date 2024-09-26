package com.github.INIT_SGGW.MonoTanksClient.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.NoArgsConstructor;
import java.io.IOException;

@NoArgsConstructor
public class CamelCaseEnumSerializer extends JsonSerializer<Enum<?>> {

    @Override
    public void serialize(Enum<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String camelCaseName = toCamelCase(value.name());
        gen.writeString(camelCaseName);
    }

    private String toCamelCase(String s) {
        String[] parts = s.toLowerCase().split("_");
        StringBuilder camelCaseString = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(parts[i].substring(0, 1).toUpperCase());
            camelCaseString.append(parts[i].substring(1).toLowerCase());
        }
        return camelCaseString.toString();
    }
}
