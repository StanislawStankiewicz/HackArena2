package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.INIT_SGGW.MonoTanksClient.utils.CamelCaseEnumDeserializer;
import com.github.INIT_SGGW.MonoTanksClient.utils.CamelCaseEnumSerializer;

import lombok.RequiredArgsConstructor;

/**
 * Represents the direction in which a tank, turrent or bullet is facing.
 */
@RequiredArgsConstructor
@JsonSerialize(using = CamelCaseEnumSerializer.class)
@JsonDeserialize(using = CamelCaseEnumDeserializer.class)
public enum Direction {
    /**
     * Represents the upward direction.
     */
    UP,

    /**
     * Represents the rightward direction.
     */
    RIGHT,

    /**
     * Represents the downward direction.
     */
    DOWN,

    /**
     * Represents the leftward direction.
     */
    LEFT;
}