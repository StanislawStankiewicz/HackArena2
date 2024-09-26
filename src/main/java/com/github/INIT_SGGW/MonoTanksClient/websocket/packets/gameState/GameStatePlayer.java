package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.INIT_SGGW.MonoTanksClient.utils.OptionalDeserializer;

import java.util.Optional;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record GameStatePlayer(
        String id,
        String nickname,
        long color,
        @JsonDeserialize(using = OptionalDeserializer.class) Optional<Long> ping,
        @JsonDeserialize(using = OptionalDeserializer.class) Optional<Long> score,
        @JsonDeserialize(using = OptionalDeserializer.class) Optional<Long> ticksToRegen) {
}