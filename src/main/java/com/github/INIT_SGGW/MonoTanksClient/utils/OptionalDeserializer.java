package com.github.INIT_SGGW.MonoTanksClient.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Optional;

public class OptionalDeserializer extends JsonDeserializer<Optional<?>> {
    @Override
    public Optional<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isNull()) {
            return Optional.empty();
        } else if (node.isBoolean()) {
            return Optional.of(node.asBoolean());
        } else if (node.isLong()) {
            return Optional.of(node.asLong());
        } else if (node.isInt()) {
            return Optional.of(node.asInt());
        } else if (node.isDouble()) {
            return Optional.of(node.asDouble());
        } else if (node.isTextual()) {
            return Optional.of(node.asText());
        }
        return Optional.empty();
    }
}
