package com.github.INIT_SGGW.MonoTanksBot.Bot.aggressive;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Action;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Zone;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class GameStateUtils {

    private final GameState gameState;

    private boolean isWall(int x, int y) {
        return gameState.map().tiles()[x][y].getEntities()
                .stream().anyMatch(entity -> entity instanceof Tile.Wall);
    }

    public Point getRandomPoint(String id) {
        Zone[] zones = gameState.map().zones();

        Point ourLocation = getTankPosition(id);

        // pick further zone
        Zone zone = zones[0];
        long distance = 0;
        for (Zone z : zones) {
            System.out.println("Zone:");
            System.out.println(z.x + " " + z.y);
            long x = z.x - ourLocation.x;
            long y = z.y - ourLocation.y;
            long d = x * x + y * y;
            if (d > distance) {
                distance = d;
                zone = z;
            }
        }
        return new Point((int) zone.y, (int)zone.x);
    }

    public Action getMoveToGetToPoint(String id, Point targetPoint) {
        // Find our tank
        Tile.Tank ourTank = findTankById(id);
        if (ourTank == null) {
            // Tank not found
            return Action.PASS;
        }

        // Get the tank's current position
        Point startPoint = getTankPosition(ourTank);
        if (startPoint == null) {
            // Position not found
            return Action.PASS;
        }

        // Find path using A*
        List<Point> path = findPath(startPoint, targetPoint);
        if (path == null || path.size() < 2) {
            // No path found or already at the target
            return Action.PASS;
        }

        // Get the next point in the path
        Point nextPoint = path.get(1);

        // Determine the action to take
        return determineNextAction(ourTank, startPoint, nextPoint);
    }

    public Tile.Tank findTankById(String id) {
        Tile[][] tiles = gameState.map().tiles();
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                for (Tile.TileEntity entity : tiles[x][y].getEntities()) {
                    if (entity instanceof Tile.Tank tank) {
                        if (tank.getOwnerId().equals(id)) {
                            return tank;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Point getTankPosition(Tile.Tank tank) {
        Tile[][] tiles = gameState.map().tiles();
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (tiles[x][y].getEntities().contains(tank)) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public Point getTankPosition(String id) {
        return getTankPosition(findTankById(id));
    }

    private List<Point> findPath(Point start, Point goal) {
        Set<Point> closedSet = new HashSet<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fScore));
        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);
        openSet.add(new Node(start, heuristicCostEstimate(start, goal)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.point.equals(goal)) {
                return reconstructPath(cameFrom, current.point);
            }

            closedSet.add(current.point);

            for (Point neighbor : getNeighbors(current.point)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                int tentativeGScore = gScore.get(current.point) + 1; // Assuming uniform cost
                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current.point);
                    gScore.put(neighbor, tentativeGScore);
                    int fScore = tentativeGScore + heuristicCostEstimate(neighbor, goal);
                    openSet.add(new Node(neighbor, fScore));
                }
            }
        }
        // Path not found
        return null;
    }

    private static class Node {
        Point point;
        int fScore;

        Node(Point point, int fScore) {
            this.point = point;
            this.fScore = fScore;
        }
    }

    private List<Point> getNeighbors(Point point) {
        List<Point> neighbors = new ArrayList<>();
        int x = point.x;
        int y = point.y;
        int[][] directions = {
                {-1, 0}, // Up
                {1, 0},  // Down
                {0, -1}, // Left
                {0, 1}   // Right
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Check boundaries
            if (newX >= 0 && newX < gameState.map().tiles().length &&
                    newY >= 0 && newY < gameState.map().tiles()[0].length) {
                if (!isWall(newX, newY)) {
                    neighbors.add(new Point(newX, newY));
                }
            }
        }
        return neighbors;
    }

    private int heuristicCostEstimate(Point a, Point b) {
        // Manhattan distance
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private List<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        List<Point> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private Action determineNextAction(Tile.Tank tank, Point currentPos, Point nextPos) {
        int dx = nextPos.x - currentPos.x;
        int dy = nextPos.y - currentPos.y;

        // Determine the desired direction
        Direction desiredDirection;
        if (dx == -1 && dy == 0) {
            desiredDirection = Direction.UP;
        } else if (dx == 1 && dy == 0) {
            desiredDirection = Direction.DOWN;
        } else if (dx == 0 && dy == -1) {
            desiredDirection = Direction.LEFT;
        } else if (dx == 0 && dy == 1) {
            desiredDirection = Direction.RIGHT;
        } else {
            // Invalid movement
            return Action.PASS;
        }

        // Compare with tank's current direction
        Direction currentDirection = tank.getDirection();
        if (currentDirection == desiredDirection) {
            return Action.MOVE_FORWARD;
        } else {
            // Need to rotate tank
            return getRotationAction(currentDirection, desiredDirection);
        }
    }

    private Action getRotationAction(Direction current, Direction desired) {
        // Map directions to integers for easy calculation
        Map<Direction, Integer> dirMap = Map.of(
                Direction.UP, 0,
                Direction.RIGHT, 1,
                Direction.DOWN, 2,
                Direction.LEFT, 3
        );

        int currentIndex = dirMap.get(current);
        int desiredIndex = dirMap.get(desired);

        int diff = (desiredIndex - currentIndex + 4) % 4;

        if (diff == 1) {
            return Action.ROTATE_TANK_RIGHT;
        } else if (diff == 3) {
            return Action.ROTATE_TANK_LEFT;
        } else if (diff == 2) {
            // 180-degree turn, can choose either direction
            return Action.ROTATE_TANK_LEFT; // Or ROTATE_TANK_RIGHT
        } else {
            return Action.PASS;
        }
    }

    public AggressorBot.State determineState(String id) {
        Tile.Tank ourTank = findTankById(id);
        if (ourTank == null) {
            // Tank not found; default to WANDERING
            return AggressorBot.State.WANDERING;
        }

        // Get the list of visible enemy tanks
        List<Tile.Tank> visibleEnemies = getVisibleEnemyTanks(id);
        if (visibleEnemies.isEmpty()) {
            // No visible tanks
            return AggressorBot.State.WANDERING;
        }

        // Define the radius
        int radius = 3; // Adjust as needed

        // Get our tank's position
        Point ourPosition = getTankPosition(ourTank);

        // Check if any enemy tank is further than the radius
        for (Tile.Tank enemyTank : visibleEnemies) {
            Point enemyPosition = getTankPosition(enemyTank);
            int distance = Math.abs(ourPosition.x - enemyPosition.x) + Math.abs(ourPosition.y - enemyPosition.y);
            if (distance > radius) {
                return AggressorBot.State.APPROACHING;
            }
        }

        // Check our tank's bullet count
        int bullets = ourTank.getTurret().bulletCount().orElse(0L).intValue();
        if (bullets > 0) {
            return AggressorBot.State.ATTACKING;
        } else {
            return AggressorBot.State.CIRCLING;
        }
    }

    private List<Tile.Tank> getVisibleEnemyTanks(String ourTankId) {
        List<Tile.Tank> enemyTanks = new ArrayList<>();
        Tile[][] tiles = gameState.map().tiles();

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                Tile tile = tiles[x][y];
                if (!tile.isVisible()) {
                    continue; // Skip non-visible tiles
                }
                for (Tile.TileEntity entity : tile.getEntities()) {
                    if (entity instanceof Tile.Tank tank) {
                        if (!tank.getOwnerId().equals(ourTankId)) {
                            enemyTanks.add(tank);
                        }
                    }
                }
            }
        }
        return enemyTanks;
    }

    public Point getClosestEnemyPosition(String ourTankId) {
        // Find our tank
        Tile.Tank ourTank = findTankById(ourTankId);
        if (ourTank == null) {
            // Our tank not found
            return null;
        }

        // Get our tank's position
        Point ourPosition = getTankPosition(ourTank);
        if (ourPosition == null) {
            // Our tank's position not found
            return null;
        }

        // Get the list of visible enemy tanks
        List<Tile.Tank> visibleEnemies = getVisibleEnemyTanks(ourTankId);
        if (visibleEnemies.isEmpty()) {
            // No visible enemy tanks
            return null; // Or handle as appropriate
        }

        // Initialize variables to track the closest enemy
        Point closestEnemyPosition = null;
        int minDistance = Integer.MAX_VALUE;

        // Iterate over visible enemy tanks to find the closest one
        for (Tile.Tank enemyTank : visibleEnemies) {
            // Get enemy tank's position
            Point enemyPosition = getTankPosition(enemyTank);
            if (enemyPosition == null) {
                // Enemy tank's position not found
                continue;
            }

            // Calculate the Manhattan distance between our tank and the enemy tank
            int distance = Math.abs(ourPosition.x - enemyPosition.x) + Math.abs(ourPosition.y - enemyPosition.y);

            // Update the closest enemy if this one is closer
            if (distance < minDistance) {
                minDistance = distance;
                closestEnemyPosition = enemyPosition;
            }
        }

        // Return the position of the closest enemy tank
        return closestEnemyPosition;
    }

    public boolean isTurretFacingEnemy(Tile.Tank ourTank, Point ourPosition, Point enemyPosition) {
        Direction turretDirection = ourTank.getTurret().direction();
        int dx = enemyPosition.x - ourPosition.x; // Vertical difference
        int dy = enemyPosition.y - ourPosition.y; // Horizontal difference

        switch (turretDirection) {
            case UP:
                return dx < 0 && dy == 0;
            case DOWN:
                return dx > 0 && dy == 0;
            case LEFT:
                return dy < 0 && dx == 0;
            case RIGHT:
                return dy > 0 && dx == 0;
            default:
                return false;
        }
    }

    public Action getTurretRotationAction(Tile.Tank ourTank, Point ourPosition, Point enemyPosition) {
        if (isTurretFacingEnemy(ourTank, ourPosition, enemyPosition)) {
            return Action.PASS; // Turret is already facing the enemy
        }

        // Determine desired turret direction
        Direction desiredDirection = getDirectionTowards(ourPosition, enemyPosition);
        Direction currentTurretDirection = ourTank.getTurret().direction();

        // Get action to rotate turret towards desired direction
        return getTurretRotation(currentTurretDirection, desiredDirection);
    }

    private Action getTurretRotation(Direction current, Direction desired) {
        // Map directions to integers for easy calculation
        Map<Direction, Integer> dirMap = Map.of(
                Direction.UP, 0,
                Direction.RIGHT, 1,
                Direction.DOWN, 2,
                Direction.LEFT, 3
        );

        int currentIndex = dirMap.get(current);
        int desiredIndex = dirMap.get(desired);

        int diff = (desiredIndex - currentIndex + 4) % 4;

        if (diff == 1) {
            return Action.ROTATE_TURRET_RIGHT;
        } else if (diff == 3) {
            return Action.ROTATE_TURRET_LEFT;
        } else if (diff == 2) {
            // 180-degree turn, choose either direction
            return Action.ROTATE_TURRET_LEFT;
        } else {
            return Action.PASS;
        }
    }

    public boolean isAligned(Point ourPosition, Point enemyPosition) {
        return ourPosition.x == enemyPosition.x || ourPosition.y == enemyPosition.y;
    }

    public Action getMoveToAlignWithEnemy(String id, Point enemyPosition) {
        // Find our tank
        Tile.Tank ourTank = findTankById(id);
        if (ourTank == null) {
            return Action.PASS;
        }

        // Get our tank's position
        Point ourPosition = getTankPosition(ourTank);
        if (ourPosition == null) {
            return Action.PASS;
        }

        // Find the path to align with the enemy
        List<Point> alignmentPoints = getAlignmentPoints(ourPosition, enemyPosition);

        // Choose the closest alignment point
        Point targetPoint = null;
        int minDistance = Integer.MAX_VALUE;

        for (Point point : alignmentPoints) {
            List<Point> path = findPath(ourPosition, point);
            if (path != null && path.size() < minDistance) {
                minDistance = path.size();
                targetPoint = point;
            }
        }

        if (targetPoint != null) {
            return getMoveToGetToPoint(id, targetPoint);
        } else {
            // Cannot align; decide alternative action
            return Action.PASS;
        }
    }

    private List<Point> getAlignmentPoints(Point ourPosition, Point enemyPosition) {
        List<Point> points = new ArrayList<>();

        // Points along the same row
        if (ourPosition.x != enemyPosition.x) {
            for (int y = 0; y < gameState.map().tiles()[0].length; y++) {
                Point point = new Point(enemyPosition.x, y);
                if (!isWall(point.x, point.y) && !isOccupied(point.x, point.y)) {
                    points.add(point);
                }
            }
        }

        // Points along the same column
        if (ourPosition.y != enemyPosition.y) {
            for (int x = 0; x < gameState.map().tiles().length; x++) {
                Point point = new Point(x, enemyPosition.y);
                if (!isWall(point.x, point.y) && !isOccupied(point.x, point.y)) {
                    points.add(point);
                }
            }
        }

        return points;
    }

    private boolean isOccupied(int x, int y) {
        return gameState.map().tiles()[x][y].getEntities()
                .stream().anyMatch(entity -> entity instanceof Tile.Tank || entity instanceof Tile.Wall);
    }

    private Direction getDirectionTowards(Point from, Point to) {
        int dx = to.x - from.x; // Vertical difference
        int dy = to.y - from.y; // Horizontal difference

        if (dx < 0 && dy == 0) {
            return Direction.UP;
        } else if (dx > 0 && dy == 0) {
            return Direction.DOWN;
        } else if (dx == 0 && dy < 0) {
            return Direction.LEFT;
        } else if (dx == 0 && dy > 0) {
            return Direction.RIGHT;
        } else {
            // Not aligned; pick a direction to move closer
            if (Math.abs(dx) > Math.abs(dy)) {
                return dx < 0 ? Direction.UP : Direction.DOWN;
            } else {
                return dy < 0 ? Direction.LEFT : Direction.RIGHT;
            }
        }
    }
}
