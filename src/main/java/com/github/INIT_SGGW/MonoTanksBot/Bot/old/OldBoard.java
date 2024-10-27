//package com.github.INIT_SGGW.MonoTanksBot.Bot.old;
//
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Map;
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Zone;
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
//import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Turret;
//import lombok.Getter;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import static com.github.INIT_SGGW.MonoTanksBot.Bot.old.ScoreWeights.COLLIDING_BULLET_WEIGHT;
//
//public class OldBoard {
//    private GameState currentGameState;
//    private List<Site> sites;
//    //array of distances to all sites (get the size from GameState)
//    private BoardDistances boardDistances;
//
//    private String myId;
//
//    public record Bullet(int x, int y, Direction direction) {
//    }
//
//    public record GroundItem(int x, int y, Tile.Item item) {
//    }
//
//    private List<int[]> mines;
//    private List<int[]> lasers;
//    private List<Bullet> bullets;
//    private List<GroundItem> items;
//    @Getter
//    private List<Tank> tanks = new ArrayList<>();
//    @Getter
//    private Tank ourTank;
//
//    public OldBoard(GameState gameState, String myId) {
//        this.currentGameState = gameState;
//        sites = new ArrayList<>();
//        this.myId = myId;
//        boardDistances = new BoardDistances(gameState.map().tiles()[0].length, gameState.map().tiles().length, gameState.map().zones().length, gameState);
//        analyzeBoard();
//    }
//
//    private OldBoard() {}
//
//    public void update(GameState gameState) {
//        currentGameState = gameState;
//        analyzeBoard();
//    }
//
//    // adds objects to corresponding lists
//    private void analyzeBoard() {
//        Tile[][] tiles = currentGameState.map().tiles();
//        Tile tile;
//        resetArrayLists(); // todo check if objects changed do not delete in fog of war
//        for (int x = 0; x < currentGameState.map().tiles().length; x++) {
//            for (int y = 0; y < currentGameState.map().tiles()[x].length; y++) {
//                tile = tiles[x][y];
//                for (Tile.TileEntity entity : tile.getEntities()) {
//                    if (entity instanceof Tile.Mine mine) {
//                        mines.add(new int[]{x, y});
//                    } else if (entity instanceof Tile.Bullet bullet) {
//                        bullets.add(new Bullet(x, y, Direction.from(bullet.getDirection())));
//                    } else if (entity instanceof Tile.Laser laser) {
//                        lasers.add(new int[]{x, y});
//                    } else if (entity instanceof Tile.Item item) {
//                        items.add(new GroundItem(x, y, item));
//                    } else if (entity instanceof Tile.Tank tank) {
//                        updateTanks(tank, x, y);
//                    }
//                }
//            }
//        }
//    }
//
//    private void updateTanks(Tile.Tank tank, int x, int y) {
//        Tank newTank = new Tank(
//                tank.getOwnerId(),
//                x,
//                y,
//                Direction.from(tank.getDirection()),
//                Direction.from(tank.getTurret().direction()),
//                !tank.getOwnerId().equals(myId),
//                tank.getTurret().bulletCount().orElse(0L).intValue()
//        );
//
//        boolean tankExists = false;
//        for (int i = 0; i < tanks.size(); i++) {
//            if (tanks.get(i).getId().equals(newTank.getId())) {
//                tanks.set(i, newTank);
//                tankExists = true;
//                break;
//            }
//        }
//
//        if (!tankExists) {
//            tanks.add(newTank);
//        }
//
//        if (newTank.getId().equals(myId)) {
//            ourTank = newTank;
//        }
//    }
//
//    public void updateSites() {
//        for (int i = 0; i < currentGameState.map().zones().length; i++) {
//            Zone zone = currentGameState.map().zones()[i];
//            Site.Owner owner = Site.Owner.NONE;
//            if (zone.status instanceof Zone.ZoneStatus.Captured capturedStatus) {
//                if (capturedStatus.playerId.equals(myId)) {
//                    owner = Site.Owner.US;
//                } else {
//                    owner = Site.Owner.ENEMY;
//                }
//            } else if (zone.status instanceof Zone.ZoneStatus.Neutral) {
//                owner = Site.Owner.NONE;
//            } else if (zone.status instanceof Zone.ZoneStatus.BeingRetaken || zone.status instanceof Zone.ZoneStatus.BeingCaptured) {
//                owner = Site.Owner.ENEMY;
//
//            } else if (zone.status instanceof Zone.ZoneStatus.BeingContested) {
//                owner = Site.Owner.ENEMY;
//
//            }
//            if (sites.stream().filter(site -> site.index == zone.index - 65).count() == 0) {
//                sites.add(new Site(zone.index - 65, owner));
//            } else {
//                sites.stream().filter(site -> site.index == zone.index - 65).findFirst().get().setOwner(owner);
//            }
//        }
//    }
//
//    private void resetArrayLists() {
//        mines = new ArrayList<>();
//        lasers = new ArrayList<>();
//        bullets = new ArrayList<>();
//        items = new ArrayList<>();
//    }
//
//    public float evaluateBoard() {
//        float score = 0;
//
//        //get all data needed for evaluation
//        score += evaluateBullets(currentGameState.map().tiles().length);
//
//        // evaluate rotation - plus if heading a better field
//
//        //site evaluation
//        score += evaluateSites();
//
//        return score;
//    }
//
//    private float evaluateBullets(int mapSize) {
//        float result = 0;
//        for (Bullet bullet : bullets) {
//            float distance = getDistanceFromBullet(bullet, ourTank);
//            if (distance == -1) {
//                continue;
//            }
//            result -= (mapSize - distance) * COLLIDING_BULLET_WEIGHT;
//        }
//        return result;
//    }
//
//    public float evaluateSites() {
//        float result = 0;
//        for (Site site : sites) {
//            if (site.owner == Site.Owner.US) {
//                result += (float) (0.0 * boardDistances.getDistance(ourTank.getX(), ourTank.getY())[site.index]);
//            } else if (site.owner == Site.Owner.ENEMY) {
//                result += (float) (-1 * boardDistances.getDistance(ourTank.getX(), ourTank.getY())[site.index]);
//            } else if (site.owner == Site.Owner.NONE) {
//                result += (float) (-0.75 * boardDistances.getDistance(ourTank.getX(), ourTank.getY())[site.index]);
//            }
//        }
//        return result;
//    }
//
//    public float isBulletOnCollisionCourse(Bullet bullet, Tank myTank) {
//        if (bullet.y == myTank.getY()) {
//            if (bullet.direction == Direction.UP && bullet.x > myTank.getX()) {
//                getDistanceFromBullet(bullet, myTank);
//            } else if (bullet.direction == Direction.DOWN && bullet.x < myTank.getX()) {
//                getDistanceFromBullet(bullet, myTank);
//            }
//        } else if (bullet.x == myTank.getX()) {
//            if (bullet.direction == Direction.LEFT && bullet.y > myTank.getY()) {
//                return getDistanceFromBullet(bullet, myTank);
//            } else if (bullet.direction == Direction.RIGHT && bullet.y < myTank.getY()) {
//                return getDistanceFromBullet(bullet, myTank);
//            }
//        }
//        return -1;
//    }
//
//    private float getDistanceFromBullet(Bullet bullet, Tank tank) {
//        return Math.abs(bullet.x - tank.getX()) + Math.abs(bullet.y - tank.getY());
//    }
//
//    public void applyMoveToTank(String tankId, Action action) {
//        Tank targetTank = tanks.stream().filter(t -> Objects.equals(t.getId(), tankId)).findFirst().get();
//        Tile[][] tiles = currentGameState.map().tiles();
//        Tile tile = tiles[targetTank.getX()][targetTank.getY()];
//
//        switch (action) {
//            case Action.MOVE_FORWARD -> {
//                moveTank(tiles, targetTank, targetTank.getXYInFront());
//            }
//            case Action.MOVE_BACKWARD -> {
//                moveTank(tiles, targetTank, targetTank.getXYBehind());
//            }
//            case Action.ROTATE_TANK_RIGHT -> {
//                rotateTank(tile, targetTank, Direction.RIGHT);
//            }
//            case Action.ROTATE_TANK_LEFT -> {
//                rotateTank(tile, targetTank, Direction.LEFT);
//            }
//            case Action.ROTATE_TURRET_RIGHT -> {
//                ourTank.setShootingDirection(ourTank.getShootingDirection().turnRight());
//            }
//            case Action.ROTATE_TURRET_LEFT -> {
//                ourTank.setShootingDirection(ourTank.getShootingDirection().turnLeft());
//            }
//            case Action.DROP_MINE -> {
//                tile.getEntities().add(new Tile.Mine());
//            }
//            case Action.PASS -> {
//                //pass
//            }
//            case Action.USE_FIRE_BULLET -> {
//                //create a new bulent 1 in front of turret dirrection with direction = turret direction
//
//            }
//            case Action.USE_FIRE_DOUBLE_BULLET -> {
//                //same as above
//            }
//            case Action.USE_LASER -> {
//                //create a new laser in front of turret direction
//            }
//            case Action.USE_RADAR -> {
//                // cant simulate this but its kinda free and reveals lot of stuff, probably return 2nd highest score
//                // (so we avoid death first then use util)
//            }
//        }
//    }
//
//    private void moveTank(Tile[][] tiles, Tank targetTank, int[] coords){
//        //todo move only the tank and not the whole tile
//        Tile tankTile = tiles[targetTank.getX()][targetTank.getY()];
//        tiles[targetTank.getX()][targetTank.getY()] = tiles[coords[0]][coords[1]];
//        tiles[coords[0]][coords[1]] = tankTile;
//    }
//
//    private void rotateTank(Tile tile, Tank tank, Direction direction) {
//        Direction newDirection = Direction.turn(tank.getDrivingDirection(), direction);
//        tile.getEntities().stream()
//                .filter(entity -> entity instanceof Tile.Tank)
//                .map(entity -> (Tile.Tank) entity)
//                .filter(entity -> entity.getOwnerId().equals(tank.getId()))
//                .findFirst()
//                .ifPresent(entity -> entity.setDirection(Direction.to(newDirection)));
//    }
//
//    private void rotateTurret(Tile tile, Tank tank, Direction direction) {
//        Direction newDirection = Direction.turn(tank.getShootingDirection(), direction);
//        tile.getEntities().stream()
//                .filter(entity -> entity instanceof Tile.Tank)
//                .map(entity -> (Tile.Tank) entity)
//                .filter(entity -> entity.getOwnerId().equals(tank.getId()))
//                .findFirst()
//                .ifPresent(e -> new Turret(Direction.to(newDirection), e.getTurret().bulletCount(), e.getTurret().ticksToRegenBullet()));
//    }
//
//    public OldBoard deepCopy() {
//        OldBoard newBoard = new OldBoard();
//        newBoard.myId = this.myId;
//        newBoard.currentGameState = copyGameState();
//        newBoard.sites = new ArrayList<>(this.sites);
//        newBoard.boardDistances = this.boardDistances;
//        newBoard.mines = new ArrayList<>(this.mines);
//        newBoard.lasers = new ArrayList<>(this.lasers);
//        newBoard.bullets = new ArrayList<>(this.bullets);
//        newBoard.items = new ArrayList<>(this.items);
//        return newBoard;
//    }
//
//    private GameState copyGameState() {
//        Tile[][] tiles = currentGameState.map().tiles();
//        Tile[][] tilesCopy = new Tile[tiles.length][tiles[0].length];
//        for (Tile[] row : tiles) {
//            for (Tile tile : row) {
//                tilesCopy[tile.getX()][tile.getY()] = tile;
//            }
//        }
//        Map mapCopy = new Map(tilesCopy, currentGameState.map().zones());
//        return new GameState(mapCopy, currentGameState.tick(), currentGameState.turn(), currentGameState.gameStatus());
//    }
//}
