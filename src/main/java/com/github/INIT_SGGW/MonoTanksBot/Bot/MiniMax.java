package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity.TankWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiniMax {


    public static Action getBestAction(Board board, int depth) {
        Action bestAction = null;
        int bestScore = Integer.MIN_VALUE;

        TankWrapper tank = board.getTank(MyBot.id);
        List<Action> actions = board.getFeasibleActions(tank);
        List<Map<Action, Integer>> actionScores = new ArrayList<>();
        for (Action action : actions) {
            Board newBoard = board.copy();
            newBoard.perform(action, tank);
//            int score = BoardEvaluator.evaluateBoard(newBoard);
            int score = minimax(newBoard, depth - 1);
            if (action == Action.MOVE_FORWARD) {
                score += 5;
            }
            actionScores.add(Map.of(action, score));
            if (score > bestScore) {
                bestScore = score;
                bestAction = action;
            }
        }
        System.out.println("Action scores: " + actionScores);
        return bestAction;
    }

    private static int minimax(Board board, int depth) {
        if (depth == 0) {
            return BoardEvaluator.evaluateBoard(board);
        }

        int bestScore = Integer.MIN_VALUE;

        TankWrapper tank = board.getTank(MyBot.id);
        List<Action> actions = board.getFeasibleActions(tank);
        for (Action action : actions) {
            Board newBoard = board.copy();
            newBoard.perform(action, tank);
            int score = minimax(newBoard, depth - 1);
            if (score > bestScore) {
                bestScore = score;
            }
        }
        return bestScore;
    }


}
