package com.github.INIT_SGGW.MonoTanksClient.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Optional;

public class OptionalDeserializer extends JsonDeserializer<Optional<Long>> {
    @Override
    public Optional<Long> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return Optional.ofNullable(p.readValueAs(Long.class));
    }
}