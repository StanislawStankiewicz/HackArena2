package com.github.INIT_SGGW.MonoTanksBot.Bot;

import java.util.Optional;
import java.util.Random;

import com.github.INIT_SGGW.MonoTanksBot.Bot.util.BoardPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.Bot;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData.LobbyData;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.Config.MAX_DEPTH;

public class MyBot extends Bot {
    private static final Logger logger = LoggerFactory.getLogger(MyBot.class);

    public static String id;

    public MyBot(LobbyData lobbyData) {
        super(lobbyData);
        id = lobbyData.playerId();
    }

    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {
        // TODO Auto-generated method stub
    }

    @Override
    public BotResponse nextMove(GameState gameState) {
        Board board = new Board(gameState);

        Action bestAction = Action.PASS;

        try {
            bestAction = MiniMax.getBestAction(board, MAX_DEPTH);
            System.out.println("Best action: " + bestAction);
        } catch (Exception e) {
            logger.error("Error in MiniMax", e);
        }

        return MoveMap.moveToResponse.get(bestAction);
    }

    @Override
    public void onWarningReceived(Warning warning, Optional<String> message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGameEnd(GameEnd gameEnd) {
        // TODO Auto-generated method stub
    }
}
