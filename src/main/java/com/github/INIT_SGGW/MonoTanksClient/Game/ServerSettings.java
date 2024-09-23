package com.github.INIT_SGGW.MonoTanksClient.Game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerSettings {
    @JsonProperty("gridDimension")
    private int gridDimension;

    @JsonProperty("numberOfPlayers")
    private int numberOfPlayers;

    @JsonProperty("seed")
    private int seed;

    @JsonProperty("broadcastInterval")
    private int broadcastInterval;

    @JsonProperty("eagerBroadcast")
    private boolean eagerBroadcast;

    @JsonProperty("ticks")
    private long ticks;
}


