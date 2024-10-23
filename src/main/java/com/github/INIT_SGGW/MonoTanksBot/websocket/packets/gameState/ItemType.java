package com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.INIT_SGGW.MonoTanksBot.utils.CamelCaseEnumDeserializer;
import com.github.INIT_SGGW.MonoTanksBot.utils.CamelCaseEnumSerializer;

/**
 * Enum representing the different types of items that can be found on the game
 * board.
 */
@JsonSerialize(using = CamelCaseEnumSerializer.class)
@JsonDeserialize(using = CamelCaseEnumDeserializer.class)
public enum ItemType {
    UNKNOWN,
    LASER,
    DOUBLE_BULLET,
    RADAR,
    MINE;
}
