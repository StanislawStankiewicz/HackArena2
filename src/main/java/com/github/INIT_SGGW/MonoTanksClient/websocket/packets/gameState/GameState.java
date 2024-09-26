package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile;
import lombok.Data;

@Data
public class GameState {

    @JsonProperty("tick")
    private final float tick;
    @JsonProperty("players")
    private final GameStatePlayer[] players;
    @JsonProperty("map")
    @JsonDeserialize(using = MapDeserializer.class)
    private final Tile[][] map;
    @JsonProperty("zones")
    private final Zone[] zones;
    @JsonProperty("id")
    private String id;

    @JsonCreator
    public GameState(
            @JsonProperty("id") String id,
            @JsonProperty("time") float tick,
            @JsonProperty("players") GameStatePlayer[] players,
            @JsonProperty("map") @JsonDeserialize(using = MapDeserializer.class) Tile[][] map,
            @JsonProperty("zones") Zone[] zones) {
        this.id = id;
        this.tick = tick;
        this.players = players;
        this.map = map;
        this.zones = zones;
    }
}