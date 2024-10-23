package com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Record representing the lobby data.
 */
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record LobbyData(
        /**
         * The ID of the player.
         */
        String playerId,

        /**
         * Array of players in the lobby.
         */
        LobbyPlayer[] players,

        /**
         * Settings of the server.
         */
        ServerSettings serverSettings) {
}