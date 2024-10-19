package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import java.util.Optional;

import com.github.INIT_SGGW.MonoTanksClient.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.lobbyData.LobbyData;

/**
 * Abstract class representing an AI agent.
 */
public abstract class Agent {

    /**
     * Constructor for the Agent class.
     *
     * @param lobbyData Initial lobby data used to create the agent.
     */
    public Agent(LobbyData lobbyData) {
    }

    /**
     * Method to handle subsequent lobby data received after the initial data.
     *
     * @param lobbyData The subsequent lobby data.
     */
    public abstract void onSubsequentLobbyData(LobbyData lobbyData);

    /**
     * Method to determine the next move of the agent based on the current game
     * state.
     *
     * @param gameState The current state of the game.
     * @return An AgentResponse representing the agent's next move.
     */
    public abstract AgentResponse nextMove(GameState gameState);

    /**
     * Called when a warning is received from the server.
     * Please, do remember that if your agent is stuck on processing warning,
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
