package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Site;
import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Tank;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.Simulation.getFeasableMoves;

public class Board {
    private List<Site> sites;
    private final List<Tank> tanks = new ArrayList<>();
    private String myId;


    public Board(GameState gameState, String myId) {
//        calculateSitePositions(gameState);
        this.myId = myId;
        createTanks(gameState);
        tanks.forEach(System.out::println);
//        updateBoard(gameState);
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

    private void calculateSitePositions(GameState gameState){
        int x = 0, y = 0;
        for (Tile[] row : gameState.map().tiles()) {
            for (Tile tile : row) {
                //calculate the position of sites
                if (tile.getZoneIndex().isPresent()) {
                    int zoneIndex = tile.getZoneIndex().get();
                    if (sites.size() <= zoneIndex) {
                        sites.add(new Site(zoneIndex));
                    }
                    Site site = sites.get(zoneIndex);
                    site.addTile(x, y);
                }

                y++;
            }
            x++;
        }
        sites.forEach(Site::averagePosition);
    }

    private void updateBoard(GameState gameState) {
        //update the board with the current state of the game
        for (Tile[] row : gameState.map().tiles()) {
            for (Tile tile : row) {
                // get all entities
                List<Tile.TileEntity> entities = tile.getEntities();
            }
        }
    }
}
