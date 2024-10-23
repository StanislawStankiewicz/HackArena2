package com.github.INIT_SGGW.MonoTanksBot.BotAbstraction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.INIT_SGGW.MonoTanksBot.utils.CamelCaseEnumDeserializer;
import com.github.INIT_SGGW.MonoTanksBot.utils.CamelCaseEnumSerializer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@JsonSerialize(using = CamelCaseEnumSerializer.class)
@JsonDeserialize(using = CamelCaseEnumDeserializer.class)
public enum AbilityType {
    FIRE_BULLET,
    FIRE_DOUBLE_BULLET,
    USE_LASER,
    USE_RADAR,
    DROP_MINE;
}