package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.lobbyData;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record ServerSettings(
        int gridDimension,
        int numberOfPlayers,
        int seed,
        int broadcastInterval,
        boolean eagerBroadcast,
        long ticks) {
}