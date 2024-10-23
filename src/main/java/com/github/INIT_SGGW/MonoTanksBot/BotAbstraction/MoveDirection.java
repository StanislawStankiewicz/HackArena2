package com.github.INIT_SGGW.MonoTanksBot.BotAbstraction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.INIT_SGGW.MonoTanksBot.utils.CamelCaseEnumDeserializer;
import com.github.INIT_SGGW.MonoTanksBot.utils.CamelCaseEnumSerializer;

import lombok.RequiredArgsConstructor;

/**
 * Represents the direction of tank movement.
 */
@RequiredArgsConstructor
@JsonSerialize(using = CamelCaseEnumSerializer.class)
@JsonDeserialize(using = CamelCaseEnumDeserializer.class)
public enum MoveDirection {
    /**
     * Represents a forward movement direction.
     */
    FORWARD,

    /**
     * Represents a backward movement direction.
     */
    BACKWARD;
}