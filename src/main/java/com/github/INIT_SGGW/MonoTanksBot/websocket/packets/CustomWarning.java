package com.github.INIT_SGGW.MonoTanksBot.websocket.packets;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record CustomWarning(String message) {
}
