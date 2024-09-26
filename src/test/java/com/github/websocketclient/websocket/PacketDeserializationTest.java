package com.github.websocketclient.websocket;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.INIT_SGGW.MonoTanksClient.websocket.Packet;
import com.github.INIT_SGGW.MonoTanksClient.websocket.PacketType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PacketDeserializationTest {
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
    }

    @Test
    public void testPacketDeserializationSimple() throws JsonProcessingException {
        String json = "{\"type\": \"gameState\"}";
        Packet packet = this.mapper.readValue(json, Packet.class);

        assertEquals(PacketType.GAME_STATE, packet.getType());
        assertEquals(null, packet.getPayload());
    }


    // Serialization test of a Pong packet. So we convert class into a string. It also should have empty payload.
    @Test
    public void testPacketSerializationSimple() throws JsonProcessingException {
        Packet packet = new Packet(PacketType.PONG, new ObjectMapper().createObjectNode());
        String json = this.mapper.writeValueAsString(packet);

        assertEquals("{\"type\":\"pong\"}", json);
    }
}