package com.github.INIT_SGGW.MonoTanksBot.Bot.aggressive;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Action;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.Comparator;
import java.util.List;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.Config.RADIUS_TO_ORBIT;


public class AggressorBot{

    public long tick;
    public String id;
    public Point targetPoint;
    public GameStateUtils utils;

    public State state;

    public AggressorBot(String id) {
        this.id = id;
        this.state = State.WANDERING;
        this.tick = 0;
    }

    public Action nextMove(GameState gameState) {
        this.tick++;
        utils = new GameStateUtils(gameState);
        state = utils.determineState(id);
        if (state == State.WANDERING) {
            return wander();
        }
        if (state == State.APPROACHING) {
            return approach();
        }
        if (state == State.ATTACKING) {
            return attack();
        }
        if (state == State.CIRCLING) {
            return circle();
        }
        return null;
    }

    private Action circle() {
        // Get our tank
        Tile.Tank ourTank = utils.findTankById(id);
        if (ourTank == null) {
            return Action.PASS;
        }

        // Get the closest enemy tank
        Tile.Tank enemyTank = utils.getClosestEnemyTank(id);
        if (enemyTank == null) {
            // No visible enemies; default to wandering
            state = State.WANDERING;
            return wander();
        }

        // Get positions around the enemy tank at radius 2
        Point enemyPosition = utils.getTankPosition(enemyTank.getOwnerId());
        List<Point> positions = utils.getPositionsAround(enemyPosition, RADIUS_TO_ORBIT);

        // Filter out positions that are in the line of fire of the enemy's barrel
        positions.removeIf(pos -> utils.isInEnemyBarrelLine(enemyTank, pos));

        // Filter out positions that are walls or occupied
        positions.removeIf(pos -> utils.isWall(pos.x, pos.y) || utils.isOccupied(pos.x, pos.y));

        if (positions.isEmpty()) {
            // No safe positions; default to attack or other action
            return attack();
        }

        // Get our position
        Point ourPosition = utils.getTankPosition(id);
        if (ourPosition == null) {
            return Action.PASS;
        }

        Point circlingTargetPoint = utils.getTankPosition(id);

        // If we have reached the circlingTargetPoint or if it is null
        if (circlingTargetPoint == null || ourPosition.equals(circlingTargetPoint)) {
            // Sort positions by angle around the enemy tank
            positions.sort(Comparator.comparingDouble(pos -> utils.angleBetween(enemyPosition, pos)));

            // Find the index of our current position in the sorted list
            int index = -1;
            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).equals(ourPosition)) {
                    index = i;
                    break;
                }
            }

            int nextIndex;
            if (index != -1) {
                // If we are on one of the positions, pick the next one (clockwise)
                nextIndex = (index + 1) % positions.size();
            } else {
                // If we are not on one of the positions, pick the closest one
                positions.sort(Comparator.comparingInt(pos -> utils.manhattanDistance(ourPosition, pos)));
                nextIndex = 0;
            }

            circlingTargetPoint = positions.get(nextIndex);
        }

        // Move towards the circlingTargetPoint
        return utils.getMoveToGetToPoint(id, circlingTargetPoint);
    }

    private Action attack() {
        // Get our tank
        Tile.Tank ourTank = utils.findTankById(id);
        if (ourTank == null) {
            return Action.PASS;
        }

        // Get the closest enemy position
        Point enemyPosition = utils.getClosestEnemyPosition(id);
        if (enemyPosition == null) {
            // No visible enemies; default to wandering
            state = State.WANDERING;
            return wander();
        }

        // Get our tank's position
        Point ourPosition = utils.getTankPosition(id);

        // Keep the turret rotated towards the enemy
        Action turretAction = utils.getTurretRotationAction(ourTank, ourPosition, enemyPosition);
        if (turretAction != Action.PASS) {
            return turretAction;
        }

        // Check if we are aligned with the enemy
        if (utils.isAligned(ourPosition, enemyPosition)) {
            // Fire if we have bullets
            if (ourTank.getTurret().bulletCount().orElse(0L) > 0) {
                return Action.USE_FIRE_BULLET;
            } else {
                // No bullets; consider reloading or other action
                return Action.PASS;
            }
        } else {
            // Move to get aligned with the enemy
            return utils.getMoveToAlignWithEnemy(id, enemyPosition);
        }
    }

    private Action wander() {
        if (tick % 10 == 0) {
            return Action.ROTATE_TURRET_LEFT;
        }
        if (utils.getTankPosition(id).equals(targetPoint)) {
            targetPoint = null;
        }
        if (targetPoint == null) {
            targetPoint = utils.getRandomPoint(id);
            System.out.println("New target point: " + targetPoint);
        }
        return utils.getMoveToGetToPoint(id, targetPoint);
    }

    private Action approach() {
        return utils.getMoveToGetToPoint(id, utils.getClosestEnemyPosition(id));
    }

    enum State {
        WANDERING,
        APPROACHING,
        ATTACKING,
        CIRCLING
    }
}
