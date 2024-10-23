package com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState;

import java.util.Optional;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.INIT_SGGW.MonoTanksBot.utils.OptionalDeserializer;

/**
 * Represents a player in the game state.
 */
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record GameStatePlayer(
        /**
         * The unique identifier for the player.
         */
        String id,

        /**
         * The nickname of the player.
         */
        String nickname,

        /**
         * The color associated with the player, represented as a long value. In format
         * 0xAABBGGRR.
         */
        long color,

        /**
         * The ping of the player.
         */
        Long ping,

        /**
         * The score of the player, represented as an Optional Long.
         * You only see your own score.
         */
        @JsonDeserialize(using = OptionalDeserializer.class) Optional<Long> score,

        /**
         * The number of ticks remaining for the player to regenerate, represented as an
         * Optional Long.
         */
        @JsonDeserialize(using = OptionalDeserializer.class) Optional<Long> ticksToRegen,

        /**
         * Whether the player is using radar.
         * This is only available for our own player.
         */
        @JsonDeserialize(using = OptionalDeserializer.class) Optional<Boolean> isUsingRadar) {
}
