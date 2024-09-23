//package com.github.INIT_SGGW.MonoTanksClient.websocket;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonValue;
//
//public enum PacketType {
//    Ping(1),
//    Pong(2),
//    GameState(21),
//    LobbyData(31),
//
//    TankMovement(11),
//    TankRotation(12),
//    TankShoot(13);
//
//    private final int value;
//
//    PacketType(int value) {
//        this.value = value;
//    }
//
//    @JsonValue
//    public int getValue() {
//        return value;
//    }
//
//    @JsonCreator
//    public static PacketType fromValue(int value) {
//        for (PacketType type : PacketType.values()) {
//            if (type.value == value) {
//                return type;
//            }
//        }
//        throw new IllegalArgumentException("Unknown enum value: " + value);
//    }
//}

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

    LOBBY_DATA,
    GAME_START,

    GAME_STATE,
    TANK_MOVEMENT,
    TANK_ROTATION,
    TANK_SHOOT,

    GAME_END,

    ALREADY_MADE_MOVEMENT;
}