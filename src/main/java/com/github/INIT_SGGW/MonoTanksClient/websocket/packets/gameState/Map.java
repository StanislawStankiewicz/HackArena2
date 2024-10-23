package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState;

import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile;

/**
 * Represents the map in the game state.
 *
 * @param tiles The tiles on the map.
 * @param zones The zones on the map.
 */
public record Map(
        Tile[][] tiles,
        Zone[] zones) {
}
