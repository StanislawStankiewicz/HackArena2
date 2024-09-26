package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.lobbyData;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record LobbyData(
        String playerId,
        LobbyPlayer[] players,
        ServerSettings serverSettings) {
}