package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a player at the end of a game.
 */
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record GameEndPlayer(
        /**
         * The unique identifier of the player.
         */
        String id,

        /**
         * The nickname of the player.
         */
        String nickname,

        /**
         * The color associated with the player.
         */
        long color,

        /**
         * The score of the player.
         */
        long score) {
}