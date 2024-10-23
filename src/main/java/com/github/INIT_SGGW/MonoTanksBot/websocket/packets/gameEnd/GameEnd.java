package com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents the end of a game.
 */
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record GameEnd(
        /**
         * An array of players at the end of the game.
         */
        GameEndPlayer[] players) {
}