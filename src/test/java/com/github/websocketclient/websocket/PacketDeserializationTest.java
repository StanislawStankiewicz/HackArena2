package com.github.websocketclient.websocket;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.INIT_SGGW.MonoTanksClient.websocket.Packet;
import com.github.INIT_SGGW.MonoTanksClient.websocket.PacketType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PacketDeserializationTest {

    @Test
    public void testPacketDeserializationSimple() throws JsonProcessingException {
        String json = "{\"type\": \"gameState\"}";
        Packet packet = new ObjectMapper().readValue(json, Packet.class);

        assertEquals(PacketType.GAME_STATE, packet.getType());
        assertEquals("{}", packet.getPayload().toString());
    }

    // Serialization test of a Pong packet. So we convert class into a string. It also should have empty payload.
    @Test
    public void testPacketSerializationSimple() throws JsonProcessingException {
        Packet packet = new Packet(PacketType.PONG, new ObjectMapper().createObjectNode());
        String json = new ObjectMapper().writeValueAsString(packet);

        assertEquals("{\"type\":\"pong\"}", json);
    }
}