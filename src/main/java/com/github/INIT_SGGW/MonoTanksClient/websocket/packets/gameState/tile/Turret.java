package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.INIT_SGGW.MonoTanksClient.utils.OptionalDeserializer;

import java.util.Optional;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record Turret(
        Direction direction,
        @JsonDeserialize(using = OptionalDeserializer.class) Optional<Long> bulletCount,
        @JsonDeserialize(using = OptionalDeserializer.class) Optional<Double> ticksToRegenBullet) {
}