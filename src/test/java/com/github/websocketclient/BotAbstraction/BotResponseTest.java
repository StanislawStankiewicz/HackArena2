package com.github.websocketclient.BotAbstraction;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.AbilityType;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.RotationDirection;

public class BotResponseTest {
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
    }

    @Test
    public void testMoveSerialization() throws JsonProcessingException {
        BotResponse.Move movePayload = new BotResponse.Move(MoveDirection.FORWARD);
        String json = mapper.writeValueAsString(movePayload);

        JsonNode expectedJson = mapper.readTree("{\"type\":\"movement\",\"payload\":{\"direction\":\"forward\"}}");
        JsonNode actualJson = mapper.readTree(json);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testRotationSerialization() throws JsonProcessingException {
        BotResponse.RotationResponse rotationPayload = new BotResponse.RotationResponse(
                Optional.of(RotationDirection.RIGHT), Optional.empty());
        String json = mapper.writeValueAsString(rotationPayload);

        JsonNode expectedJson = mapper
                .readTree("{\"type\":\"rotation\",\"payload\":{\"tankRotation\":\"right\",\"turretRotation\":null}}");
        JsonNode actualJson = mapper.readTree(json);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testAbilityUseSerialization() throws JsonProcessingException {
        BotResponse.AbilityUse abilityUsePayload = new BotResponse.AbilityUse(AbilityType.FIRE_BULLET);
        String json = mapper.writeValueAsString(abilityUsePayload);

        JsonNode expectedJson = mapper
                .readTree("{\"type\":\"abilityUse\",\"payload\":{\"abilityType\":\"fireBullet\"}}");
        JsonNode actualJson = mapper.readTree(json);

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testResponsePassSerialization() throws JsonProcessingException {
        BotResponse.ResponsePass passPayload = new BotResponse.ResponsePass();
        String json = mapper.writeValueAsString(passPayload);

        JsonNode expectedJson = mapper.readTree("{\"type\":\"pass\"}");
        JsonNode actualJson = mapper.readTree(json);

        assertEquals(expectedJson, actualJson);
    }
}
