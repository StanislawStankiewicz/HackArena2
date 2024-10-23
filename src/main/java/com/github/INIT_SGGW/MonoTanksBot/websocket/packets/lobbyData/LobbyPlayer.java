package com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Record representing a player in the lobby.
 */
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record LobbyPlayer(
        /**
         * The ID of the player.
         */
        String id,

        /**
         * The nickname of the player.
         */
        String nickname,

        /**
         * The color associated with the player.
         */
        long color) {
}