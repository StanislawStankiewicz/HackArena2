//package com.github.INIT_SGGW.MonoTanksBot.Bot;
//
//import com.github.INIT_SGGW.MonoTanksBot.Bot.util.BoardPrinter;
//import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.*;
//import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity.BulletWrapper;
//import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity.TankWrapper;
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
//import lombok.Getter;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class Board {
//
//    private GameStateWrapper gameState;
//    @Getter
//    private DistanceTable distanceTable;
//
//    public boolean isWall(int x, int y) {
//        return gameState.isWall(x, y);
//    }
//
//    public List<ZoneWrapper> getZones() {
//        return gameState.getZones();
//    }
//
//    public List<TankWrapper> getTanks() {
//        return gameState.getTanks();
//    }
//
//    public int getWidth() {
//        return gameState.getTableOfEntities()
//                .getWidth();
//    }
//
//    public int getHeight() {
//        return gameState.getTableOfEntities()
//                .getHeight();
//    }
//
//    public Board(GameState gameState) {
//        this.gameState = new GameStateWrapper(gameState);
//        this.distanceTable = new DistanceTable(this);
//
//        System.out.println("Im at " + getTank(MyBot.id));
//        System.out.println("Possible moves:");
//        System.out.println(ActionFinder.getActions(this.gameState, getTank(MyBot.id)));
//    }
//
//    private Board(GameStateWrapper gameStateWrapper, DistanceTable distanceTable) {
//        this.gameState = gameStateWrapper;
//        this.distanceTable = distanceTable;
//    }
//
//    public TankWrapper getTank(String id) {
//        return gameState.getTanks().stream().filter(tank -> tank.getId().equals(id)).findFirst().orElse(null);
//    }
//
//    public int countBulletsFlyingAt(TankWrapper tank) {
//        //check all 4 directions of the tank see if any bullets are flying at it
//        int x = tank.getX();
//        int y = tank.getY();
//        int count = 0;
//        for (DirectionWrapper direction : DirectionWrapper.values()) {
//            int dx = direction == DirectionWrapper.RIGHT ? 1 : 0;
//            dx -= direction == DirectionWrapper.LEFT ? 1 : 0;
//            int dy = direction == DirectionWrapper.DOWN ? 1 : 0;
//            dy -= direction == DirectionWrapper.UP ? 1 : 0;
//            int newX = x + dx;
//            int newY = y + dy;
//            while (newX >= 0 && newX < gameState.getTableOfEntities().getWidth() && newY >= 0 && newY < gameState.getTableOfEntities().getHeight()) {
//                if (gameState.getTableOfEntities().get(newX, newY).stream().anyMatch(entity -> entity instanceof BulletWrapper && ((BulletWrapper) entity).getDirection() == direction)) {
//                    count++;
//                }
//                newX += dx;
//                newY += dy;
//            }
//        }
//        return count;
//    }
//
//    public TankWrapper findClosestEnemy(TankWrapper tank) {
//        TankWrapper closest = null;
//        int minDistance = Integer.MAX_VALUE;
//
//        for (TankWrapper enemy : gameState.getTanks()) {
//            if (enemy.getId().equals(tank.getId())) {
//                continue;
//            }
//            int distance = (int) Math.sqrt(Math.pow(tank.getX() - enemy.getX(), 2) + Math.pow(tank.getY() - enemy.getY(), 2));
//            if (distance < minDistance) {
//                minDistance = distance;
//                closest = enemy;
//            }
//        }
//        return closest;
//    }
//
//    public List<Action> getFeasibleActions(TankWrapper tank) {
//        return ActionFinder.getActions(gameState, tank);
//    }
//
//    public void perform(Action action, TankWrapper tank) {
//        gameState.perform(action, tank);
//    }
//
//    public Board copy() {
//        Board clone = new Board(gameState.deepClone(), distanceTable);
//
//        return clone;
//    }
//
//    public ZoneWrapper[] getClosestZones(TankWrapper tank) {
//        ZoneWrapper[] closestZones = new ZoneWrapper[getZones().size()];
//        int[] distances = new int[getZones().size()];
//        for (int i = 0; i < getZones().size(); i++) {
//            ZoneWrapper zone = getZones().get(i);
//            distances[i] = distanceTable.distances.get(tank.getX(), tank.getY()).get(zone);
//            closestZones[i] = zone;
//        }
//        for (int i = 0; i < getZones().size(); i++) {
//            for (int j = i + 1; j < getZones().size(); j++) {
//                if (distances[i] > distances[j]) {
//                    int temp = distances[i];
//                    distances[i] = distances[j];
//                    distances[j] = temp;
//                    ZoneWrapper tempZone = closestZones[i];
//                    closestZones[i] = closestZones[j];
//                    closestZones[j] = tempZone;
//                }
//            }
//        }
//        return closestZones;
//    }
//}
