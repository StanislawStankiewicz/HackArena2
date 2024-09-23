package com.github.INIT_SGGW.MonoTanksClient.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class Packet {

    @JsonProperty("type")
    private PacketType type;

    @JsonProperty("payload")
    private JsonNode payload;


    public PacketType getType() {
        return type;
    }

    public JsonNode getPayload() {
        return payload;
    }
}
