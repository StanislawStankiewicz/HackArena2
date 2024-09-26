package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.lobbyData.LobbyData;

public abstract class Agent {
    public Agent(LobbyData lobbyData) {
    }

    public abstract void onSubsequentLobbyData(LobbyData lobbyData);

    public abstract AgentResponse nextMove(GameState gameState);

    public abstract void onGameEnd(GameEnd gameEnd);
}