package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Map;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Site> sites;


    public Board(GameState gameState) {

    }

    public int evaluateBoard(GameState gameState) {
        int score = 0;
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
                // get all entities
                List<Tile.TileEntity> entities = tile.getEntities();

                //calculate the position of sites
                if (tile.getZoneIndex().isPresent()) {
                    int zoneIndex = tile.getZoneIndex().get();

                }

                y++;
            }
            x++;
        }
    }
    
    private int incomingMissilesDistance(GameState gameState, String ownerId) {
        Tile[][] tiles = gameState.map().tiles();

        int[][] ownerPosition = findOwnerPosition(tiles, ownerId);
    }

    private int[][] findOwnerPosition(Tile[][] tiles, String ownerId) {
        int x = 0, y = 0;
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                tile.getEntities();
                y++;
            }
            x++;
        }
        return new int[][]{{-1, -1}};
    }
}
