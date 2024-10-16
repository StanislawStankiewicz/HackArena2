package com.github.INIT_SGGW.MonoTanksClient.Agent;

import java.util.Optional;

import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.AbilityType;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.Agent;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.AgentResponse;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction.RotationDirection;
import com.github.INIT_SGGW.MonoTanksClient.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameEnd.GameEndPlayer;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile.Tank;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile.Tile.TileEntity;
import com.github.INIT_SGGW.MonoTanksClient.websocket.packets.lobbyData.LobbyData;

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
    public void onGameStarting() {

    }

    @Override
    public AgentResponse nextMove(GameState gameState) {

        // Find my tank
        Tank myTank = null;
        for (Tile[] row : gameState.map()) {
            for (Tile tile : row) {
                for (TileEntity object : tile.getEntities()) {
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
    public void onWarningReceived(Warning warning, Optional<String> message) {
        switch (warning) {
            case PLAYER_ALREADY_MADE_ACTION_WARNING -> {
                System.out.println("[System] ⚠️ Player already made action warning");
            }
            case MISSING_GAME_STATE_ID_WARNING -> {
                System.out.println("[System] ⚠️ Missing game state ID warning");
            }
            case SLOW_RESPONSE_WARNING -> {
                System.out.println("[System] ⚠️ Slow response warning");
            }
            case ACTION_IGNORED_DUE_TO_DEAD_WARNING -> {
                System.out.println("[System] ⚠️ Action ignored due to dead warning");
            }
            case CUSTOM_WARNING -> {
                String msg = message.orElse("No message");
                System.out.println("[System] ⚠️ Custom Warning: " + msg);
            }
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
