package com.github.INIT_SGGW.MonoTanksClient.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Packet {

    @JsonProperty("type")
    private PacketType type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("payload")
    private JsonNode payload;
}
