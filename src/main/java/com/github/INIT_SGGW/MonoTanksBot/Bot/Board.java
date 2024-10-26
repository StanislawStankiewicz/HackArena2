package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Site;
import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Tank;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {
    private List<Site> sites;
    //array of distances to all sites (get the size from GameState)
    private BoardDistances boardDistances;

    private final List<Tank> tanks = new ArrayList<>();
    private String myId;

    private final List<int[]> walls = new ArrayList<>();
    private final List<int[]> mines = new ArrayList<>();
    private final List<int[]> bullets = new ArrayList<>();



    public Board(GameState gameState, String myId) {

        this.myId = myId;
        boardDistances = new BoardDistances(gameState.map().tiles()[0].length, gameState.map().tiles().length, gameState.map().zones().length, gameState);
        analyzeBoard(gameState);
        tanks.forEach(System.out::println);
//        updateBoard(gameState);
        //print board distances in a 2D array
        for (int i = 0; i < gameState.map().tiles()[0].length; i++) {
            for (int j = 0; j < gameState.map().tiles().length; j++) {
                int distance = (int) boardDistances.getDistance(i,j)[1];
                if (distance == 2147483647) {
                    System.out.printf("%2s ", "X");
                } else {
                    System.out.printf("%2d ", distance);
                }
            }
            System.out.println();
        }
    }

    public void update(GameState gameState){
        
    }

    private void analyzeBoard(GameState gameState) {
        Tile[][] tiles = gameState.map().tiles();
        Tile tile;
        for (int x = 0; x < gameState.map().tiles().length; x++) {
            for (int y = 0; y < gameState.map().tiles()[x].length; y++) {
                tile = tiles[x][y];
                if (tile.getEntities().isEmpty()) {
                    continue;
                }
                for (Tile.TileEntity entity : tile.getEntities()) {
                    if (entity instanceof Tile.Wall wall) {
                        walls.add(new int[]{x, y});
                    }
                    if (entity instanceof Tile.Mine mine) {
                        mines.add(new int[]{x, y});
                    }
                    if (entity instanceof Tile.Bullet bullet) {
                        bullets.add(new int[]{x, y});
                    }
                }

            }
        }

    }

    record TankInfo(int x, int y, Tile.Tank tank) {
    }

    private void createTanks(GameState gameState) {
        // find tank on board
        findTanks(gameState)
                .forEach(tankInfo -> {
                    Tank tank = new Tank(
                            tankInfo.tank().getOwnerId(),
                            tankInfo.x(),
                            tankInfo.y(),
                            Direction.from(tankInfo.tank().getDirection()),
                            Direction.from(tankInfo.tank().getTurret().direction()),
                            !tankInfo.tank().getOwnerId().equals(myId),
                            tankInfo.tank().getTurret().bulletCount().orElse(0L).intValue()
                    );
                    tanks.add(tank);
                });
    }

    private List<TankInfo> findTanks(GameState gameState) {
        Tile[][] tiles = gameState.map().tiles();

        List<TankInfo> result = new ArrayList<>();

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                // get all entities
                Tile tile = gameState.map().tiles()[x][y];
                List<Tile.TileEntity> entities = tile.getEntities();

                if (entities.isEmpty()) {
                    continue;
                }

                Optional<Tile.Tank> optTank = entities.stream()
                        .filter(e -> e instanceof Tile.Tank)
                        .map(e -> (Tile.Tank) e)
                        .findFirst();

                if (optTank.isPresent()) {
                    result.add(new TankInfo(x, y, optTank.get()));
                }
            }
        }

        return result;
    }

    public int evaluateBoard(GameState gameState) {
        int score = 0;

        //get all data needed for evaluation
        for (Tile[] row : gameState.map().tiles()) {
            for (Tile tile : row) {
                // get all entities
                List<Tile.TileEntity> entities = tile.getEntities();
            }
        }
        return score;
    }
}
