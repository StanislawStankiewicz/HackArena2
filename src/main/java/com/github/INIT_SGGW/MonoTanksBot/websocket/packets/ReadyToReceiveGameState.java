package com.github.INIT_SGGW.MonoTanksBot.websocket.packets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.INIT_SGGW.MonoTanksBot.websocket.PacketType;

import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ReadyToReceiveGameState {

    /**
     * Converts the response to a PacketType.
     * 
     * @return the PacketType of the response.
     */
    @JsonProperty("type")
    public PacketType toPacketType() {
        return PacketType.READY_TO_RECEIVE_GAME_STATE;
    }

}
