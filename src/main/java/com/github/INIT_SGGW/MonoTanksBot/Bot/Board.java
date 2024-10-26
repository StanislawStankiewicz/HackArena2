package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Site;
import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Tank;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Zone;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.ScoreWeights.COLLIDING_BULLET_WEIGHT;

public class Board {
    private List<Site> sites;
    //array of distances to all sites (get the size from GameState)
    private BoardDistances boardDistances;

    private String myId;

    public record Bullet(int x, int y, Direction direction) {
    }

    public record GroundItem(int x, int y, Tile.Item item) {
    }

    private List<int[]> mines;
    private List<int[]> lasers;
    private List<Bullet> bullets;
    private List<GroundItem> items;
    @Getter
    private List<Tank> tanks = new ArrayList<>();
    @Getter
    private Tank ourTank;
    private GameState currentGameState;

    public Board(GameState gameState, String myId) {
        sites = new ArrayList<>();
        this.myId = myId;
        boardDistances = new BoardDistances(gameState.map().tiles()[0].length, gameState.map().tiles().length, gameState.map().zones().length, gameState);
        analyzeBoard(gameState);
        System.out.println("us: " + getOurTank());
//        updateBoard(gameState);
        //print board distances in a 2D array
//        printBoardDistances(gameState);
    }

    public void update(GameState gameState){
        analyzeBoard(gameState);
        bullets.forEach((bullet) -> System.out.println(isBulletOnCollisionCourse(bullet, ourTank)));
    }

    // adds objects to corresponding lists
    private void analyzeBoard(GameState gameState) {
        Tile[][] tiles = gameState.map().tiles();
        Tile tile;
        resetArrayLists();
        for (int x = 0; x < gameState.map().tiles().length; x++) {
            for (int y = 0; y < gameState.map().tiles()[x].length; y++) {
                tile = tiles[x][y];
                if (tile.getEntities().isEmpty()) {
                    continue;
                }
                for (Tile.TileEntity entity : tile.getEntities()) {
                    if (entity instanceof Tile.Mine mine) {
                        mines.add(new int[]{x, y});
                    } else
                    if (entity instanceof Tile.Bullet bullet) {
                        bullets.add(new Bullet(x, y, Direction.from(bullet.getDirection())));
                    } else
                    if (entity instanceof Tile.Laser laser) {
                        lasers.add(new int[]{x, y});
                    } else
                    if (entity instanceof Tile.Item item) {
                        items.add(new GroundItem(x, y, item));
                    }
                    else if (entity instanceof Tile.Tank tank) {
                        updateTanks(tank, x, y);
                    }
                }
            }
        }
    }

    private void updateTanks(Tile.Tank tank, int x, int y) {
        Tank newTank = new Tank(
                tank.getOwnerId(),
                x,
                y,
                Direction.from(tank.getDirection()),
                Direction.from(tank.getTurret().direction()),
                !tank.getOwnerId().equals(myId),
                tank.getTurret().bulletCount().orElse(0L).intValue()
        );

        boolean tankExists = false;
        for (int i = 0; i < tanks.size(); i++) {
            if (tanks.get(i).getId().equals(newTank.getId())) {
                tanks.set(i, newTank);
                tankExists = true;
                break;
            }
        }

        if (!tankExists) {
            tanks.add(newTank);
        }

        if (newTank.getId().equals(myId)) {
            ourTank = newTank;
        }
    }

    public void updateSites(GameState gameState) {
        for (int i = 0; i < gameState.map().zones().length; i++) {
            Zone zone = gameState.map().zones()[i];
            Site.Owner owner = Site.Owner.NONE;
            if (zone.status instanceof Zone.ZoneStatus.Captured capturedStatus) {
                if (capturedStatus.playerId.equals(myId)) {
                    owner = Site.Owner.US;
                } else {
                    owner = Site.Owner.ENEMY;
                }
            } else if (zone.status instanceof Zone.ZoneStatus.Neutral) {
                owner = Site.Owner.NONE;
            } else if (zone.status instanceof Zone.ZoneStatus.BeingRetaken || zone.status instanceof Zone.ZoneStatus.BeingCaptured) {
                owner = Site.Owner.ENEMY;

            } else if (zone.status instanceof Zone.ZoneStatus.BeingContested) {
                owner = Site.Owner.ENEMY;

            }
            if (sites.stream().filter(site -> site.index == zone.index-65).count() == 0){
                sites.add(new Site(zone.index-65, owner));}
            else {
                sites.stream().filter(site -> site.index == zone.index-65).findFirst().get().setOwner(owner);
            }
        }
    }

    private void resetArrayLists() {
        mines = new ArrayList<>();
        lasers = new ArrayList<>();
        bullets = new ArrayList<>();
        items = new ArrayList<>();
    }

    public float evaluateBoard(GameState gameState) {
        float score = 0;

        //get all data needed for evaluation
        score += evaluateBullets(gameState.map().tiles().length);

        //site evaluation
        score += evaluateSites();

        return score;
    }

    private float evaluateBullets(int mapSize) {
        float result = 0;
        for (Bullet bullet : bullets) {
            float distance = getDistanceFromBullet(bullet, ourTank);
            if (distance == -1) {
                continue;
            }
            result -= (mapSize - distance) * COLLIDING_BULLET_WEIGHT;
        }
        return result;
    }

    public float evaluateSites(){
        float result =0;
        for (Site site : sites) {
            if (site.owner == Site.Owner.US) {
                result += (float) (0.0 * boardDistances.getDistance(ourTank.getX(), ourTank.getY())[site.index]);
            } else if (site.owner == Site.Owner.ENEMY) {
                result += (float) (-1 * boardDistances.getDistance(ourTank.getX(), ourTank.getY())[site.index]);
            } else if (site.owner == Site.Owner.NONE) {
                result += (float) (-0.75 * boardDistances.getDistance(ourTank.getX(), ourTank.getY())[site.index]);
            }
        }
        return result;
    }

    public float isBulletOnCollisionCourse(Bullet bullet, Tank myTank) {
        if (bullet.y == myTank.getY()) {
            if (bullet.direction == Direction.UP && bullet.x > myTank.getX()) {
                getDistanceFromBullet(bullet, myTank);
            } else if (bullet.direction == Direction.DOWN && bullet.x < myTank.getX()) {
                getDistanceFromBullet(bullet, myTank);
            }
        }
        else if (bullet.x == myTank.getX()) {
            if (bullet.direction == Direction.LEFT && bullet.y > myTank.getY()) {
                return getDistanceFromBullet(bullet, myTank);
            } else if (bullet.direction == Direction.RIGHT && bullet.y < myTank.getY()) {
                return getDistanceFromBullet(bullet, myTank);
            }
        }
        return -1;
    }

    private float getDistanceFromBullet(Bullet bullet, Tank tank) {
        return Math.abs(bullet.x - tank.getX()) + Math.abs(bullet.y - tank.getY());
    }

    private void printBoardDistances(GameState gameState) {
        for (int i = 0; i < gameState.map().tiles()[0].length; i++) {
            for (int j = 0; j < gameState.map().tiles().length; j++) {
                int distance = (int) boardDistances.getDistance(i,j)[1];
                if (distance == 2147483647) {
                    System.out.printf("%2s ", "X");
                } else {
                    System.out.printf("%2d ", distance);
                }
            }
        }
    }

//    private GameState applyMoveToTank(String tankId, MoveType moveType) {
//        Tank targetTank = tanks.stream().filter(t -> t.getId() == tankId).findFirst().get();
//
//        switch (moveType) {
//            case MoveType.MOVE_BACKWARD -> {
//
//            }
//        }
//    }

}
