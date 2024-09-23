package com.github.INIT_SGGW.MonoTanksClient.Game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbyData {
    @JsonProperty("playerId")
    private String playerId;

    @JsonProperty("players")
    private Player[] players;

    @JsonProperty("serverSettings")
    private ServerSettings serverSettings;
}