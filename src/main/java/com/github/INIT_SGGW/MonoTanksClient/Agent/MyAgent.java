package com.github.INIT_SGGW.MonoTanksClient.Agent;

import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.Agent;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.AgentResponse;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.RotationDirection;
import com.github.INIT_SGGW.MonoTanksClient.Game.*;

import java.util.Optional;

public class MyAgent extends Agent {

    private final String myId;

    public MyAgent(LobbyData lobbyData) {
        super(lobbyData);
        this.myId = lobbyData.getPlayerId();
    }

    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {

    }

    @Override
    public AgentResponse nextMove(GameState gameState) {
        double random = Math.random();

        if (random < 0.33) {
            if (Math.random() < 0.5) {
                return AgentResponse.createMoveResponse(MoveDirection.FORWARD);
            } else {
                return AgentResponse.createMoveResponse(MoveDirection.BACKWARD);
            }
        } else if (random < 0.66) {
            double tankRandom = Math.random();
            RotationDirection tankRotation = tankRandom < 0.33 ? RotationDirection.LEFT : tankRandom < 0.66 ? RotationDirection.RIGHT : null;

            double turretRandom = Math.random();
            RotationDirection turretRotation = turretRandom < 0.33 ? RotationDirection.LEFT : turretRandom < 0.66 ? RotationDirection.RIGHT : null;

            return AgentResponse.createRotationResponse(Optional.ofNullable(tankRotation), Optional.ofNullable(turretRotation));
        } else {
            return AgentResponse.createShootResponse();
        }

    }

    @Override
    public void onGameEnd(GameEnd gameEnd) {
        System.out.println("Game ended");
        Player winner = gameEnd.getPlayers()[0];
        if (winner.getId().equals(this.myId)) {
            System.out.println("I won!");
        }

        for (Player player : gameEnd.getPlayers()) {
            System.out.println(player.getNickname() + " - " + player.getScore());
        }
    }
}
