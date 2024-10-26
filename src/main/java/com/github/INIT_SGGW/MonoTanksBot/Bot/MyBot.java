package com.github.INIT_SGGW.MonoTanksBot.Bot;

import java.util.Optional;

import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.Bot;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData.LobbyData;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.MoveLogic.*;

public class MyBot extends Bot {
    private static final Logger logger = LoggerFactory.getLogger(MyBot.class);

    private final String id;

    public MyBot(LobbyData lobbyData) {
        super(lobbyData);
        id = lobbyData.playerId();
        System.out.println("My name is id! " + id);
    }

    @Override
    public void onSubsequentLobbyData(LobbyData lobbyData) {
        // TODO Auto-generated method stub
    }

    @Override
    public BotResponse nextMove(GameState gameState) {
//        System.out.println("-----------------------------------");
//        Utility.printMap(gameState, id);
        Board board = new Board(gameState, id);
        return moveForward();
//        return getBestMove(gameState);
    }

    @Override
    public void onWarningReceived(Warning warning, Optional<String> message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGameEnd(GameEnd gameEnd) {
        // TODO Auto-generated method stub
    }

    // MINIMAX SECTION
//
//    private BotResponse getBestMove(GameState gameState, int depth, boolean isMaximizing) {
//        BotResponse bestMove = null;
//        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
//        for (// possible moves) {
//            // create new gamestate with move applied
//            int score = minimax(newState, depth - 1, !isMaximizing);
//            if (isMaximizing && score > bestScore) {
//                bestScore = score;
//                bestMove = move;
//            } else if (!isMaximizing && score < bestScore) {
//                bestScore = score;
//                bestMove = move;
//            }
//        }
//    }
}
