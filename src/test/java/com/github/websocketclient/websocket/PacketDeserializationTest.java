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
        String json = "{\"type\": \"gameState\", \"payload\": {}}";
        Packet packet = new ObjectMapper().readValue(json, Packet.class);

        assertEquals(PacketType.GAME_STATE, packet.getType());
        assertEquals("{}", packet.getPayload().toString());
    }
}