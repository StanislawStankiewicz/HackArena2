//package com.github.INIT_SGGW.MonoTanksBot.Bot.old;
//
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.github.INIT_SGGW.MonoTanksBot.Bot.old.Action.*;
//
//public class Simulation {
//
//    public static List<Action> getFeasibleMoves(GameState gameState, Tank tank) {
//        List<Action> result = new ArrayList<>();
//
//        int[] tankPosition = {tank.getX(), tank.getY()};
//        Direction direction = tank.getDrivingDirection();
//        Direction turretDirection = tank.getShootingDirection();
//
//        if (canMoveForward(gameState, tankPosition, direction)) {
//            result.add(MOVE_FORWARD);
//        }
//        if (canMoveBackward(gameState, tankPosition, direction)) {
//            result.add(MOVE_BACKWARD);
//        }
//        if (canRotateTankLeft(gameState, tankPosition, direction)) {
//            result.add(ROTATE_TANK_LEFT);
//        }
//        if (canRotateTankRight(gameState, tankPosition, direction)) {
//            result.add(ROTATE_TANK_RIGHT);
//        }
//        if (canRotateTurretLeft(gameState, tankPosition, turretDirection)) {
//            result.add(ROTATE_TURRET_LEFT);
//        }
//        if (canRotateTurretRight(gameState, tankPosition, turretDirection)) {
//            result.add(ROTATE_TURRET_RIGHT);
//        }
//        if (canUseFireBullet(gameState, tank)) {
//            result.add(USE_FIRE_BULLET);
//        }
//        switch (tank.getSpecialItem()) {
//            case ItemType.MINE -> result.add(DROP_MINE);
//            case ItemType.DOUBLE_BULLET -> result.add(USE_FIRE_DOUBLE_BULLET);
//            case ItemType.RADAR -> result.add(USE_RADAR);
//            case ItemType.LASER -> result.add(USE_LASER);
//        }
//        return result;
//    }
//
//    private static boolean canRotateTurretRight(GameState gameState, int[] tankPosition, Direction direction) {
//        return canMoveForward(gameState, tankPosition, direction.turnRight());
//    }
//
//    private static boolean canRotateTurretLeft(GameState gameState, int[] tankPosition, Direction direction) {
//        return canMoveForward(gameState, tankPosition, direction.turnLeft());
//    }
//
//    private static boolean canRotateTankRight(GameState gameState, int[] tankPosition, Direction direction) {
//        return canMoveForward(gameState, tankPosition, direction.turnRight());
//    }
//
//    private static boolean canRotateTankLeft(GameState gameState, int[] tankPosition, Direction direction) {
//        return canMoveForward(gameState, tankPosition, direction.turnLeft());
//    }
//
//    private static boolean canMoveBackward(GameState gameState, int[] tankPosition, Direction direction) {
//        return canMoveForward(gameState, tankPosition, direction.turnAround());
//    }
//
//    private static boolean canMoveForward(GameState gameState, int[] tankPosition, Direction direction) {
//        int x = tankPosition[0];
//        int y = tankPosition[1];
//        Tile[][] tiles = gameState.map().tiles();
//        x = direction == Direction.UP ? x - 1 : x;
//        x = direction == Direction.DOWN ? x + 1 : x;
//        y = direction == Direction.LEFT ? y - 1 : y;
//        y = direction == Direction.RIGHT ? y + 1 : y;
//        if (x < 0 || x >= tiles.length || y < 0 || y >= tiles.length) {
//            return false;
//        }
//        return tiles[x][y].getEntities().stream()
//                .noneMatch(entity ->
//                        entity instanceof Tile.Wall || entity instanceof Tile.Tank || entity instanceof Tile.Laser
//                );
//    }
//
//    private static boolean canUseFireBullet(GameState gameState, Tank tank) {
//        if (tank.getBulletsAmount() == 0) {
//            return false;
//        }
//        int[] tankPosition = {tank.getX(), tank.getY()};
//        return canMoveForward(gameState, tankPosition, tank.getShootingDirection());
//    }
//}
