package com.github.INIT_SGGW.MonoTanksBot.BotAbstraction;

import java.util.Optional;

import com.github.INIT_SGGW.MonoTanksBot.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData.LobbyData;

/**
 * Abstract class representing an AI bot.
 */
public abstract class Bot {

    /**
     * Constructor for the Bot class.
     *
     * @param lobbyData Initial lobby data used to create the bot.
     */
    public Bot(LobbyData lobbyData) {
    }

    /**
     * Method to handle subsequent lobby data received after the initial data.
     *
     * @param lobbyData The subsequent lobby data.
     */
    public abstract void onSubsequentLobbyData(LobbyData lobbyData);

    /**
     * Method to determine the next move of the bot based on the current game
     * state.
     *
     * @param gameState The current state of the game.
     * @return An BotResponse representing the bot's next move.
     */
    public abstract BotResponse nextMove(GameState gameState);

    /**
     * Called when a warning is received from the server.
     * Please, do remember that if your bot is stuck on processing warning,
     * the next move won't be called and vice versa.
     *
     * @param warning The warning received from the server.
     */
    public abstract void onWarningReceived(Warning warning, Optional<String> message);

    /**
     * Method to handle the end of the game.
     *
     * @param gameEnd The final state of the game when it ends.
     */
    public abstract void onGameEnd(GameEnd gameEnd);
}
