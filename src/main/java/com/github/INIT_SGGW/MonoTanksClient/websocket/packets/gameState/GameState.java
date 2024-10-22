package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile;

/**
 * Represents the state of the game at a given tick.
 */
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record GameState(
        /**
         * The unique identifier for the game state.
         */
        String id,

        /**
         * The current tick of the game.
         */
        int tick,

        /**
         * The array of players in the game.
         */
        GameStatePlayer[] players,

        /**
         * The map of the game state, represented as a 2D array of tiles.
         */
        @JsonDeserialize(using = MapDeserializer.class) Tile[][] map,

        /**
         * The zones on the map.
         */
        Zone[] zones) {
}