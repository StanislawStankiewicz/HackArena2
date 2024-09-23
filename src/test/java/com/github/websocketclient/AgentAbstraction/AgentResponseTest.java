package com.github.websocketclient.AgentAbstraction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.RotationDirection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.Optional;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.AgentResponse;

public class AgentResponseTest {
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
    }

    @Test
    public void testMoveSerialization() throws JsonProcessingException {
        AgentResponse.Move movePayload = new AgentResponse.Move(MoveDirection.FORWARD);
        String json = mapper.writeValueAsString(movePayload);

        // Parse the JSON strings into JsonNode objects
        JsonNode expectedJson = mapper.readTree("{\"type\":\"tankMovement\",\"payload\":{\"direction\":0}}");
        JsonNode actualJson = mapper.readTree(json);

        // Compare the JsonNode objects
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testRotationSerialization() throws JsonProcessingException {
        AgentResponse.RotationResponse rotationPayload =
                new AgentResponse.RotationResponse(Optional.of(RotationDirection.RIGHT), Optional.empty());
        String json = mapper.writeValueAsString(rotationPayload);

        JsonNode expectedJson = mapper.readTree("{\"type\":\"tankRotation\",\"payload\":{\"tankRotation\":1}}");
        JsonNode actualJson = mapper.readTree(json);

        assertEquals(expectedJson, actualJson);
    }


    @Test
    public void testShootSerialization() throws JsonProcessingException {
        AgentResponse.Shoot shootPayload = new AgentResponse.Shoot();
        String json = mapper.writeValueAsString(shootPayload);

        JsonNode expectedJson = mapper.readTree("{\"type\":\"tankShoot\", \"payload\": {}}");
        JsonNode actualJson = mapper.readTree(json);

        assertEquals(expectedJson, actualJson);
    }

}