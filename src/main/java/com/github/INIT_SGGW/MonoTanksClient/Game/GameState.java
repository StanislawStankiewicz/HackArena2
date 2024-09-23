package com.github.INIT_SGGW.MonoTanksClient.Game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class GameState {

    @JsonProperty("playerId")
    private String playerId;

    @JsonProperty("tick")
    private final float tick;

    @JsonProperty("players")
    private final Player[] players;

    @JsonProperty("map")
    @JsonDeserialize(using = MapDeserializer.class)
    private final Tile[][] map;

    @JsonProperty("zones")
    private final Zone[] zones;

    @JsonCreator
    public GameState(
        @JsonProperty("playerId") String playerId,
        @JsonProperty("time") float tick,
        @JsonProperty("players") Player[] players,
        @JsonProperty("map") @JsonDeserialize(using = MapDeserializer.class) Tile[][] map,
        @JsonProperty("zones") Zone[] zones
    ) {
        this.playerId = playerId;
        this.tick = tick;
        this.players = players;
        this.map = map;
        this.zones = zones;
    }
}