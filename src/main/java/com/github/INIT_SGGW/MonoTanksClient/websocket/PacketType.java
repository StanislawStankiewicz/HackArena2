package com.github.INIT_SGGW.MonoTanksClient.websocket;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.INIT_SGGW.MonoTanksClient.utils.CamelCaseEnumDeserializer;
import com.github.INIT_SGGW.MonoTanksClient.utils.CamelCaseEnumSerializer;

@JsonSerialize(using = CamelCaseEnumSerializer.class)
@JsonDeserialize(using = CamelCaseEnumDeserializer.class)
public enum PacketType {
    PING,
    PONG,

    CONNECTION_ACCEPTED,
    CONNECTION_REJECTED,

    LOBBY_DATA,
    LOBBY_DELETED,
    GAME_START,

    GAME_STATE,
    TANK_MOVEMENT,
    TANK_ROTATION,
    TANK_SHOOT,

    GAME_END,

    // Warnings
    PLAYER_ALREADY_MADE_ACTION_WARNING,
    MISSING_GAME_STATE_ID_WARNING,
    SLOW_RESPONSE_WARNING,

    // Errors
    INVALID_PACKET_TYPE_ERROR,
    INVALID_PACKET_USAGE_ERROR,
}