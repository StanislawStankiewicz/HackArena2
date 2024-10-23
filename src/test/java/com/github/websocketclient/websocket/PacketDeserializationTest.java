package com.github.websocketclient.websocket;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.INIT_SGGW.MonoTanksBot.websocket.Packet;
import com.github.INIT_SGGW.MonoTanksBot.websocket.PacketType;

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

    @Test
    public void testPacketSerializationSimple() throws JsonProcessingException {
        Packet packet = new Packet(PacketType.PONG, new ObjectMapper().createObjectNode());
        String json = this.mapper.writeValueAsString(packet);

        assertEquals("{\"type\":\"pong\"}", json);
    }
}