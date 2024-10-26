package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Tank;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
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

    public static List<Callable<BotResponse>> getFeasableMoves(GameState gameState, Tank tank) {
        List<Callable<BotResponse>> result = new ArrayList<>();

        int[] tankPosition = {tank.getX(), tank.getY()};
        Direction direction = tank.getDrivingDirection();
        Direction turretDirection = tank.getShootingDirection();

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
        if (canShoot(gameState, tank)) {
            result.add(MoveLogic::useFireBullet);
        }
        switch (tank.getSpecialItem()) {
            case ItemType.MINE -> result.add(MoveLogic::dropMine);
            case ItemType.DOUBLE_BULLET -> result.add(MoveLogic::useFireBullet);
            case ItemType.RADAR -> result.add(MoveLogic::useFireDoubleBullet);
            case ItemType.LASER -> result.add(MoveLogic::useLaser);
        }
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
                .noneMatch(entity ->
                        entity instanceof Tile.Wall || entity instanceof Tile.Tank || entity instanceof Tile.Laser
                );
    }

    private static boolean canShoot(GameState gameState, Tank tank) {
        if (tank.getBulletsAmount() == 0) {
            return false;
        }
        int[] tankPosition = {tank.getX(), tank.getY()};
        return canMoveForward(gameState, tankPosition, tank.getShootingDirection());
    }
}
