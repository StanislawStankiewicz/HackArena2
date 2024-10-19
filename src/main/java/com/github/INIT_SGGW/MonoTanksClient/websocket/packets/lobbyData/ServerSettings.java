package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.lobbyData;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents the settings for the server.
 */
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record ServerSettings(
        /**
         * The dimension of the grid.
         */
        int gridDimension,

        /**
         * The number of players.
         */
        int numberOfPlayers,

        /**
         * The seed value for random number generation.
         * It is used for generating the map and the initial positions of the players.
         */
        int seed,

        /**
         * The interval at which broadcasts are sent.
         */
        int broadcastInterval,

        /**
         * Whether broadcasts are sent eagerly. If true, next game state is sent
         * immediately after all players have sent their actions.
         */
        boolean eagerBroadcast,

        /**
         * The number of ticks after which the game ends.
         */
        long ticks,

        /**
         * Whether the sandbox mode is enabled.
         */
        boolean sandboxMode,

        /**
         * The tick at which the game starts. It is null in sandbox mode.
         */
        Integer tickAtStart,

        /**
         * The name of the match. It can be null.
         */
        String matchName

) {
}