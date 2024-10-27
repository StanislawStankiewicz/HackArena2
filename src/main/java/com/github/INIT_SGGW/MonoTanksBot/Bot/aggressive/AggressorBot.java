package com.github.INIT_SGGW.MonoTanksBot.Bot.aggressive;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Action;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Zone;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.Comparator;
import java.util.List;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.Config.RADIUS_TO_ORBIT;
import static com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType.LASER;
import static com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType.UNKNOWN;


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

        Tile.Tank ourTank = utils.findTankById(id);
        if (ourTank.getSecondaryItem().isPresent()) {
             Action action = switch (ourTank.getSecondaryItem().get()) {
                case RADAR -> Action.USE_RADAR;
                case MINE -> Action.DROP_MINE;
                 default -> null;
            };
             if (action != null) {
                 return action;
             }
        }

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
        if (stationaryTicks >= 500) {
            // Reset the targetPoint to a new random point
            stationaryTicks = 0;
            return switch ((int) (tick % 2)) {
                case 1 -> Action.MOVE_BACKWARD;
                default -> Action.MOVE_FORWARD;
            };
        }

        state = utils.determineState(id);
        return switch (state) {
            case CAPTURING -> capturing(gameState);
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
                return switch (ourTank.getSecondaryItem().orElse(UNKNOWN)) {
                    case LASER -> Action.USE_LASER;
                    case DOUBLE_BULLET -> Action.USE_FIRE_DOUBLE_BULLET;
                    default -> Action.USE_FIRE_BULLET;
                };
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
        if (tick % 1000 == 0) {
            targetPoint = null;
        }
        if (utils.getTankPosition(id).equals(targetPoint)) {
            // Reached the target point, transition to CAPTURING state
            targetPoint = null;
        }
        if (targetPoint == null) {
            targetPoint = utils.getRandomPoint(id);
        }
        return utils.getMoveToGetToPoint(id, targetPoint);
    }

    private Action approach() {
        return utils.getMoveToGetToPoint(id, utils.getClosestEnemyPosition(id));
    }

    private Action capturing(GameState gameState) {
        // Assume utils has methods to get capture zone and check capture status
        Zone captureZone = utils.getTargetCaptureZone(id);
        if (captureZone == null) {
            // No capture zone assigned, transition back to WANDERING
            state = State.WANDERING;
            return wander();
        }

        // Check if the zone is already captured
//        if (utils.isZoneOurs(, id)) {utils.
//            captureZone = null;
//            state = State.WANDERING;
//            targetPoint = null; // Reset target point to find a new one
//            return wander();
//        }
        for (Zone zone : gameState.map().zones()) {
            if (zone.index == captureZone.index) {
                if (!utils.isWithinZone(utils.getTankPosition(id), new Zone[]{zone})) {
                    captureZone = zone;
                    state = State.WANDERING;
                    return wander();
                }
            }
        }

        // Get our current position
        Point ourPosition = utils.getTankPosition(id);
        if (ourPosition == null) {
            return Action.PASS;
        }

        // Ensure we're within the capture zone
        if (!utils.isWithinZone(ourPosition, new Zone[] {captureZone})) {
            // Move back into the capture zone
            return utils.getMoveToPointWithinZone(id, captureZone);
        }

        return Action.ROTATE_TANK_LEFT;
    }


    enum State {
        WANDERING,
        APPROACHING,
        ATTACKING,
        CIRCLING,
        CAPTURING
    }
}
