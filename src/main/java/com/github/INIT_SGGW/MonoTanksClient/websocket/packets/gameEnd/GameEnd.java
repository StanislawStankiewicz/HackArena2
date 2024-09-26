package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record GameEnd(GameEndPlayer[] players) {
}