package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import com.github.INIT_SGGW.MonoTanksClient.Game.GameEnd;
import com.github.INIT_SGGW.MonoTanksClient.Game.GameState;
import com.github.INIT_SGGW.MonoTanksClient.Game.LobbyData;

public abstract class Agent {
    public Agent(LobbyData lobbyData) {}
    public abstract AgentResponse nextMove(GameState gameState);
    public abstract void onGameEnd(GameEnd gameEnd);
}