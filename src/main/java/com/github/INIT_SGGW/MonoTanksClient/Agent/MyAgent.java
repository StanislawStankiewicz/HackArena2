package com.github.INIT_SGGW.MonoTanksClient.Agent;

import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.Agent;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.AgentResponse;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.RotationDirection;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.AbilityType;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd.GameEndPlayer;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.lobbyData.LobbyData;
import java.util.Optional;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.ItemType;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile.Tank;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile.TilePayload;

public class MyAgent extends Agent {

    private final String myId;

    public MyAgent(LobbyData lobbyData) {
        super(lobbyData);
        this.myId = lobbyData.playerId();
    }

    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {

    }

    @Override
    public AgentResponse nextMove(GameState gameState) {

        // Find my tank
        Tank myTank = null;
        for (Tile[] row : gameState.map()) {
            for (Tile tile : row) {
                for (TilePayload object : tile.getObjects()) {
                    if (object instanceof Tank tank && tank.getOwnerId().equals(myId)) {
                        myTank = tank;
                    }
                }
            }
        }

        // If we cannot find our tank, we are dead.
        if (myTank == null) {
            return AgentResponse.createPassResponse();
        }

        double random = Math.random();

        if (random < 0.2) {
            if (Math.random() < 0.5) {
                return AgentResponse.createMoveResponse(MoveDirection.FORWARD);
            } else {
                return AgentResponse.createMoveResponse(MoveDirection.BACKWARD);
            }
        } else if (random < 0.4) {
            double tankRandom = Math.random();
            RotationDirection tankRotation = tankRandom < 0.33 ? RotationDirection.LEFT
                    : tankRandom < 0.66 ? RotationDirection.RIGHT : null;

            double turretRandom = Math.random();
            RotationDirection turretRotation = turretRandom < 0.33 ? RotationDirection.LEFT
                    : turretRandom < 0.66 ? RotationDirection.RIGHT : null;

            return AgentResponse.createRotationResponse(Optional.ofNullable(tankRotation),
                    Optional.ofNullable(turretRotation));
        } else if (random < 0.6) {
            return AgentResponse.createAbilityUseResponse(AbilityType.FIRE_BULLET);
        } else if (random < 0.7) {
            return AgentResponse.createAbilityUseResponse(AbilityType.FIRE_DOUBLE_BULLET);
        } else if (random < 0.8) {
            return AgentResponse.createAbilityUseResponse(AbilityType.USE_LASER);
        } else if (random < 0.9) {
            return AgentResponse.createAbilityUseResponse(AbilityType.USE_RADAR);
        } else if (random < 0.95) {
            return AgentResponse.createAbilityUseResponse(AbilityType.DROP_MINE);
        } else {
            return AgentResponse.createPassResponse();
        }
    }

    @Override
    public void onGameEnd(GameEnd gameEnd) {
        System.out.println("Game ended");
        GameEndPlayer winner = gameEnd.players()[0];
        if (winner.id().equals(this.myId)) {
            System.out.println("I won!");
        }

        for (GameEndPlayer player : gameEnd.players()) {
            System.out.println(player.nickname() + " - " + player.score());
        }
    }
}
