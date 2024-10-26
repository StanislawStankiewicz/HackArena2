package com.github.INIT_SGGW.MonoTanksBot.Bot;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.Bot;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.lobbyData.LobbyData;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.MoveLogic.*;
import static com.github.INIT_SGGW.MonoTanksBot.Bot.Simulation.getFeasibleMoves;

public class MyBot extends Bot {
    private static final Logger logger = LoggerFactory.getLogger(MyBot.class);

    private final String id;
    private OldBoard board;

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
        if (board != null){
            board.update(gameState);
        } else {
            board = new OldBoard(gameState, id);
        }
        float bestScore = Float.MIN_VALUE;
        MoveType bestMove = MoveType.PASS;
        float score;
        for (MoveType moveType : getFeasibleMoves(gameState, board.getOurTank())) {
            OldBoard newBoard = board.deepCopy();
            newBoard.applyMoveToTank(id, moveType);
            score = newBoard.evaluateBoard();
            if (score > bestScore) {
                bestScore = score;
                bestMove = moveType;
            }
        }
        BotResponse finalMove = getMoveOrDefault(bestMove);
        return finalMove;
    }

    private BotResponse getMoveOrDefault(MoveType move) {
        try {
            return moveMap.get(move).call();
        } catch (Exception e) {
            return BotResponse.createPassResponse();
        }
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
