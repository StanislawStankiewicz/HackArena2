package com.github.INIT_SGGW.MonoTanksBot.Bot.aggressive;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Action;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Zone;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.Comparator;
import java.util.List;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.Config.MAX_STATIONARY_TICKS;
import static com.github.INIT_SGGW.MonoTanksBot.Bot.Config.RADIUS_TO_ORBIT;


public class AggressorBot{

    public long tick;
    public String id;
    public Point targetPoint;
    public GameStateUtils utils;

    public State state;
    private Point lastPosition = null;
    private int stationaryTicks = 0;

    public AggressorBot(String id) {
        this.id = id;
        this.state = State.WANDERING;
        this.tick = 0;
    }

    public Action nextMove(GameState gameState) {
        this.tick++;
        utils = new GameStateUtils(gameState);

        // Get our current position
        Point currentPosition = utils.getTankPosition(id);
        if (currentPosition == null) {
            // Cannot find our tank's position, pass
            return Action.PASS;
        }

        // Check if we have been stationary
        if (lastPosition != null) {
            if (currentPosition.equals(lastPosition)) {
                stationaryTicks++;
            } else {
                stationaryTicks = 0;
            }
        }
        lastPosition = currentPosition;

        // If stationary for more than 10 ticks, reset target point
        if (stationaryTicks >= MAX_STATIONARY_TICKS) {
            // Reset the targetPoint to a new random point
            targetPoint = utils.getRandomPoint(id);
            stationaryTicks = 0;
            System.out.println("Bot has been stationary for 10 ticks. New target point: " + targetPoint);
        }

        state = utils.determineState(id);
        return switch (state) {
            case CAPTURING -> capturing();
            case WANDERING -> wander();
            case APPROACHING -> approach();
            case ATTACKING -> attack();
            case CIRCLING -> circle();
            default -> Action.PASS;
        };
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
            // Reached the target point, transition to CAPTURING state
            state = State.CAPTURING;
            System.out.println("Reached target point. Transitioning to CAPTURING state.");
            return Action.PASS;
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

    private Action capturing() {
        // Assume utils has methods to get capture zone and check capture status
        Zone captureZone = utils.getTargetCaptureZone(id);
        if (captureZone == null) {
            // No capture zone assigned, transition back to WANDERING
            state = State.WANDERING;
            return wander();
        }

        // Check if the zone is already captured
        if (utils.isZoneOurs(captureZone, id)) {
            state = State.WANDERING;
            targetPoint = null; // Reset target point to find a new one
            return wander();
        }

        // Get our current position
        Point ourPosition = utils.getTankPosition(id);
        if (ourPosition == null) {
            return Action.PASS;
        }

        // Ensure we're within the capture zone
        if (!utils.isWithinZone(ourPosition, captureZone)) {
            // Move back into the capture zone
            System.out.println("Out of capture zone. Moving back into the zone.");
            return utils.getMoveToPointWithinZone(id, captureZone);
        }

        // Optionally rotate turret or perform other actions to defend the capture
        if (tick % 15 == 0) { // Example: rotate turret periodically
            return Action.ROTATE_TURRET_RIGHT;
        }

        // Maintain position by possibly making minor adjustments
        return Action.PASS; // Or implement movement to stay within the zone if necessary
    }


    enum State {
        WANDERING,
        APPROACHING,
        ATTACKING,
        CIRCLING,
        CAPTURING
    }
}
