package com.github.INIT_SGGW.MonoTanksClient.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CamelCaseEnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Get the camelCase value
        String camelCaseValue = p.getText();

        // Convert camelCase value to enum constant format (e.g., thisIsAnEnum -> THIS_IS_AN_ENUM)
        String enumConstant = toEnumConstant(camelCaseValue);

        // Return the matching enum constant
        return Enum.valueOf((Class<? extends Enum>) enumClass, enumConstant);
    }

    private String toEnumConstant(String camelCase) {
        // Converts camelCase to uppercase underscore format
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        // Obtain the actual type of the enum
        JavaType type = ctxt.getContextualType();
        if (type == null) {
            type = property.getType();
        }

        Class<?> rawClass = type.getRawClass();

        // Return a new deserializer instance with the correct enum class
        return new CamelCaseEnumDeserializer((Class<? extends Enum<?>>) rawClass);
    }
}