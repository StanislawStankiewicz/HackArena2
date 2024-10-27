//package com.github.INIT_SGGW.MonoTanksBot.Bot;
//
//import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.DirectionWrapper;
//import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.GameStateWrapper;
//import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity.TankWrapper;
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.github.INIT_SGGW.MonoTanksBot.Bot.Action.*;
//
//
//public class ActionFinder {
//
//    public static List<Action> getActions(GameStateWrapper gameState, TankWrapper tank) {
//        List<Action> result = new ArrayList<>();
//
//        if (canMoveForward(gameState, tank)) {
//            result.add(MOVE_FORWARD);
//        }
//        if (canMoveBackward(gameState, tank)) {
//            result.add(MOVE_BACKWARD);
//        }
//        if (canRotateTankLeft(gameState, tank)) {
//            result.add(ROTATE_TANK_LEFT);
//        }
//        if (canRotateTankRight(gameState, tank)) {
//            result.add(ROTATE_TANK_RIGHT);
//        }
//        if (canRotateTurretLeft(gameState, tank)) {
////            result.add(ROTATE_TURRET_LEFT);
//        }
//        if (canRotateTurretRight(gameState, tank)) {
////            result.add(ROTATE_TURRET_RIGHT);
//        }
//        if (canUseFireBullet(tank)) {
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
//    private static boolean canMoveForward(GameStateWrapper gameState, TankWrapper tank) {
//        return freeSpaceInRelativeDirection(gameState, tank, tank.getBodyDirection());
//    }
//
//    private static boolean canMoveBackward(GameStateWrapper gameState, TankWrapper tank) {
//        return freeSpaceInRelativeDirection(gameState, tank, tank.getBodyDirection().turnAround());
//    }
//
//    private static boolean canRotateTankLeft(GameStateWrapper gameState, TankWrapper tank) {
//        return freeSpaceInRelativeDirection(gameState, tank, tank.getBodyDirection().turnLeft());
//    }
//
//    private static boolean canRotateTankRight(GameStateWrapper gameState, TankWrapper tank) {
//        return freeSpaceInRelativeDirection(gameState, tank, tank.getBodyDirection().turnRight());
//    }
//
//    private static boolean canRotateTurretLeft(GameStateWrapper gameState, TankWrapper tank) {
//        return freeSpaceInRelativeDirection(gameState, tank, tank.getTurretDirection().turnLeft());
//    }
//
//    private static boolean canRotateTurretRight(GameStateWrapper gameState, TankWrapper tank) {
//        return freeSpaceInRelativeDirection(gameState, tank, tank.getTurretDirection().turnRight());
//    }
//
//    private static boolean canUseFireBullet(TankWrapper tank) {
//        return tank.getBulletsCount() > 0;
//    }
//
//    private static boolean freeSpaceInRelativeDirection(GameStateWrapper gameState, TankWrapper tank, DirectionWrapper direction) {
//        int x = tank.getX();
//        int y = tank.getY();
//
//        switch (direction) {
//            case UP -> x--;
//            case DOWN -> x++;
//            case LEFT -> y--;
//            case RIGHT -> y++;
//        }
//        // check if x y in bounds
//        if (x < 0 || x >= gameState.getTableOfEntities().getHeight() || y < 0 || y >= gameState.getTableOfEntities().getWidth()) {
//            return false;
//        }
//        return !gameState.isWall(x, y) && !gameState.isTank(x, y);
//    }
//}
