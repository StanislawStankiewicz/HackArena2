package com.github.INIT_SGGW.MonoTanksBot.BotAbstraction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.INIT_SGGW.MonoTanksBot.utils.CamelCaseEnumDeserializer;
import com.github.INIT_SGGW.MonoTanksBot.utils.CamelCaseEnumSerializer;

import lombok.RequiredArgsConstructor;

/**
 * Represents the direction of rotation.
 */
@RequiredArgsConstructor
@JsonSerialize(using = CamelCaseEnumSerializer.class)
@JsonDeserialize(using = CamelCaseEnumDeserializer.class)
public enum RotationDirection {
    /**
     * Represents a left rotation direction.
     */
    LEFT,

    /**
     * Represents a right rotation direction.
     */
    RIGHT;

}