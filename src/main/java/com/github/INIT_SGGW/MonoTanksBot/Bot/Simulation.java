package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Map;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static jdk.internal.icu.lang.UCharacter.getDirection;

public class Simulation {
    public static List<Callable<BotResponse>> moves = List.of(
            MoveLogic::moveForward,
            MoveLogic::moveBackward,
            MoveLogic::rotateTankLeft,
            MoveLogic::rotateTankRight,
            MoveLogic::rotateTurretLeft,
            MoveLogic::rotateTurretRight,
            MoveLogic::useFireBullet,
            MoveLogic::useFireDoubleBullet,
            MoveLogic::useLaser,
            MoveLogic::useRadar,
            MoveLogic::dropMine,
            MoveLogic::pass
    );

    static enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public static Object getPossibleMoves (GameState gameState, int[] tankPosition) {
        List<Callable<BotResponse>> result = new ArrayList<>();

        Direction direction = getDirection(gameState, tankPosition);

        if (canMoveForward(gameState, tankPosition, direction)) {
            result.add(MoveLogic::moveForward);
        }
        if (canMoveBackward(gameState, tankPosition)) {
            result.add(MoveLogic::moveBackward);
        }
        if (canRotateTankLeft(gameState, tankPosition)) {
            result.add(MoveLogic::rotateTankLeft);
        }
        if (canRotateTankRight(gameState, tankPosition)) {
            result.add(MoveLogic::rotateTankRight);
        }
        if (canRotateTurretLeft(gameState, tankPosition)) {
            result.add(MoveLogic::rotateTurretLeft);
        }
        if (canRotateTurretRight(gameState, tankPosition)) {
            result.add(MoveLogic::rotateTurretRight);
        }
        if (canUseFireBullet(gameState, tankPosition)) {
            result.add(MoveLogic::useFireBullet);
        }
        if (canUseFireDoubleBullet(gameState, tankPosition)) {
            result.add(MoveLogic::useFireDoubleBullet);
        }
        if (canUseLaser(gameState, tankPosition)) {
            result.add(MoveLogic::useLaser);
        }
        if (canUseRadar(gameState, tankPosition)) {
            result.add(MoveLogic::useRadar);
        }
//        for (int i = 0; i < tiles.length; ++i) {
//            for (int j = 0; j < tiles.length; ++j) {
//                tiles[i][j].
//            }
//        }

    }

    private static boolean canMoveForward(GameState gameState, int[] tankPosition) {
        gameState.map().tiles()[tankPosition[0]][tankPosition[1]];
        if (gameState.)
    }


}
