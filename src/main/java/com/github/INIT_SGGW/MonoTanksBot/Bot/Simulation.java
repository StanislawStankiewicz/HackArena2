package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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
        UP, DOWN, LEFT, RIGHT;

        public Direction turnLeft() {
            return switch (this) {
                case UP -> LEFT;
                case LEFT -> DOWN;
                case DOWN -> RIGHT;
                case RIGHT -> UP;
            };
        }

        public Direction turnRight() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }

        public Direction turnAround() {
            return switch (this) {
                case UP -> DOWN;
                case DOWN -> UP;
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
            };
        }
    }

    public static Object getFeasableMoves(GameState gameState, Tank tank) {
        List<Callable<BotResponse>> result = new ArrayList<>();

        int[] tankPosition = tank.getPosition();
        Direction direction = tank.getDirection();
        Direction turretDirection = tank.getTurretDirection();

        if (canMoveForward(gameState, tankPosition, direction)) {
            result.add(MoveLogic::moveForward);
        }
        if (canMoveBackward(gameState, tankPosition, direction)) {
            result.add(MoveLogic::moveBackward);
        }
        if (canRotateTankLeft(gameState, tankPosition, direction)) {
            result.add(MoveLogic::rotateTankLeft);
        }
        if (canRotateTankRight(gameState, tankPosition, direction)) {
            result.add(MoveLogic::rotateTankRight);
        }
        if (canRotateTurretLeft(gameState, tankPosition, turretDirection)) {
            result.add(MoveLogic::rotateTurretLeft);
        }
        if (canRotateTurretRight(gameState, tankPosition, turretDirection)) {
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
        return result;
    }

    private static boolean canRotateTurretRight(GameState gameState, int[] tankPosition, Direction direction) {
        return canMoveForward(gameState, tankPosition, direction.turnRight());
    }

    private static boolean canRotateTurretLeft(GameState gameState, int[] tankPosition, Direction direction) {
        return canMoveForward(gameState, tankPosition, direction.turnLeft());
    }

    private static boolean canRotateTankRight(GameState gameState, int[] tankPosition, Direction direction) {
        return canMoveForward(gameState, tankPosition, direction.turnRight());
    }

    private static boolean canRotateTankLeft(GameState gameState, int[] tankPosition, Direction direction) {
        return canMoveForward(gameState, tankPosition, direction.turnLeft());
    }

    private static boolean canMoveBackward(GameState gameState, int[] tankPosition, Direction direction) {
        return canMoveForward(gameState, tankPosition, direction.turnAround());
    }

    private static boolean canMoveForward(GameState gameState, int[] tankPosition, Direction direction) {
        int x = tankPosition[0];
        int y = tankPosition[1];
        Tile[][] tiles = gameState.map().tiles();
        // check if the next unit in direction is tile and if tile is not a wall
        // modify x and y according to direction do that only for now
        x = direction == Direction.UP ? x - 1 : x;
        x = direction == Direction.DOWN ? x + 1 : x;
        y = direction == Direction.LEFT ? y - 1 : y;
        y = direction == Direction.RIGHT ? y + 1 : y;
        if (x < 0 || x >= tiles.length || y < 0 || y >= tiles.length) {
            return false;
        }
        return tiles[x][y].getEntities().stream()
                .noneMatch(entity -> entity instanceof Tile.Wall);
    }

    private static boolean canUseFireBullet(GameState gameState, int[] tankPosition) {
        int x = tankPosition[0];
        int y = tankPosition[1];
        Tile[][] tiles = gameState.map().tiles();

        return tiles[x][y].getEntities().stream()
                .filter(entity -> entity instanceof Tile.Wall).findAny();
    }


}
