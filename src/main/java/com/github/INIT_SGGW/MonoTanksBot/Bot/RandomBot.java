package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.*;
import com.github.INIT_SGGW.MonoTanksBot.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEndPlayer;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile.*;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData.LobbyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class RandomBot extends Bot {
    private static final Logger logger = LoggerFactory.getLogger(RandomBot.class);

    private final String myId;

    /**
     * Constructor for the Bot class.
     *
     * @param lobbyData Initial lobby data used to create the bot.
     */
    public RandomBot(LobbyData lobbyData) {
        super(lobbyData);
        this.myId = lobbyData.playerId();
    }

    /**
     * Method to handle subsequent lobby data received after the initial data.
     *
     * @param lobbyData The subsequent lobby data.
     */
    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {

    }

    /**
     * Method to determine the next move of the bot based on the current game
     * state.
     *
     * @param gameState The current state of the game.
     * @return An BotResponse representing the bot's next move.
     */
    @Override
    public BotResponse nextMove(GameState gameState) {

//        printMap(gameState, myId);

        // Find my tank
        Tank myTank = null;
        for (Tile[] row : gameState.map().tiles()) {
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
            return BotResponse.createPassResponse();
        }

        double random = Math.random();

        if (random < 0.2) {
            if (Math.random() < 0.5) {
                return BotResponse.createMoveResponse(MoveDirection.FORWARD);
            } else {
                return BotResponse.createMoveResponse(MoveDirection.BACKWARD);
            }
        } else if (random < 0.4) {
            double tankRandom = Math.random();
            RotationDirection tankRotation = tankRandom < 0.33 ? RotationDirection.LEFT
                    : tankRandom < 0.66 ? RotationDirection.RIGHT : null;

            double turretRandom = Math.random();
            RotationDirection turretRotation = turretRandom < 0.33 ? RotationDirection.LEFT
                    : turretRandom < 0.66 ? RotationDirection.RIGHT : null;

            return BotResponse.createRotationResponse(Optional.ofNullable(tankRotation),
                    Optional.ofNullable(turretRotation));
        } else if (random < 0.6) {
            return BotResponse.createAbilityUseResponse(AbilityType.FIRE_BULLET);
        } else if (random < 0.7) {
            return BotResponse.createAbilityUseResponse(AbilityType.FIRE_DOUBLE_BULLET);
        } else if (random < 0.8) {
            return BotResponse.createAbilityUseResponse(AbilityType.USE_LASER);
        } else if (random < 0.9) {
            return BotResponse.createAbilityUseResponse(AbilityType.USE_RADAR);
        } else if (random < 0.95) {
            return BotResponse.createAbilityUseResponse(AbilityType.DROP_MINE);
        } else {
            return BotResponse.createPassResponse();
        }
    }

    /**
     * Called when a warning is received from the server.
     * Please, do remember that if your bot is stuck on processing warning,
     * the next move won't be called and vice versa.
     *
     * @param warning The warning received from the server.
     */
    @Override
    public void onWarningReceived(Warning warning, Optional<String> message) {
        switch (warning) {
            case PLAYER_ALREADY_MADE_ACTION_WARNING -> {
                logger.warn("⚠️ Player already made action warning");
            }
            case MISSING_GAME_STATE_ID_WARNING -> {
                logger.warn("⚠️ Missing game state ID warning");
            }
            case SLOW_RESPONSE_WARNING -> {
                logger.warn("⚠️ Slow response warning");
            }
            case ACTION_IGNORED_DUE_TO_DEAD_WARNING -> {
                logger.warn("⚠️ Action ignored due to dead warning");
            }
            case CUSTOM_WARNING -> {
                String msg = message.orElse("No message");
                logger.warn("⚠️ Custom Warning: {}", msg);
            }
        }
    }

    /**
     * Method to handle the end of the game.
     *
     * @param gameEnd The final state of the game when it ends.
     */
    @Override
    public void onGameEnd(GameEnd gameEnd) {
        logger.info("🏁 Game ended");
        GameEndPlayer winner = gameEnd.players()[0];
        if (winner.id().equals(this.myId)) {
            logger.info("🏆 I won!");
        }

        for (GameEndPlayer player : gameEnd.players()) {
            logger.info("👤 {} - {}", player.nickname(), player.score());
        }
    }
}
