package com.github.INIT_SGGW.MonoTanksBot.Bot.old;

import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

public class BoardDistances {
    private float[][][] distancesToSites;

    public BoardDistances(int width, int height, int numberOfSites, GameState gameState) {
        distancesToSites = new float[width][height][numberOfSites];

        //using the breath first search algorithm to calculate the distances between all sites
        //avoid walls

        //initialize the distances to infinity
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int i = 0; i < numberOfSites; i++) {
                    distancesToSites[x][y][i] = Float.POSITIVE_INFINITY;
                }
            }
        }

        //find site tiles, and recursivly calculate the distance avoiding walls
        for (int index=0;index< numberOfSites;index++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (gameState.map().tiles()[x][y].getZoneIndex().isPresent()
                            && gameState.map().tiles()[x][y].getZoneIndex().get()-65 == index) {
                        System.out.println("setting tile: " + x+","+y+ "to 0");
                        distancesToSites[x][y][index] = 0;
                        //calculate the distances on all 4 sides
                        calculateDistances(x-1, y, index, gameState, 1);
                        calculateDistances(x, y-1, index, gameState, 1);
                        calculateDistances(x, y+1, index, gameState, 1);
                        calculateDistances(x+1, y, index, gameState, 1);
                    }
                }
            }
        }
    }

    private void calculateDistances(int x, int y, int index, GameState gameState, int distance) {
        //check if the tile is out of bounds
        if (x < 0 || x >= gameState.map().tiles().length || y < 0 || y >= gameState.map().tiles()[0].length) {
            return;
        }
        //check if the tile is not a wall
        if ((gameState.map().tiles()[x][y].getZoneIndex().isPresent() && gameState.map().tiles()[x][y].getZoneIndex().get() == index)
                ||(gameState.map().tiles()[x][y].getEntities().stream().anyMatch(entity -> entity instanceof Tile.Wall))) {
            return; //if it is a wall, return
        }
        //check if the distance is smaller than the current distance
        if (distancesToSites[x][y][index] > distance) {
            distancesToSites[x][y][index] = distance;

            //calculate the distances on all 4 sides
            calculateDistances(x-1, y, index, gameState, distance+1);
            calculateDistances(x, y-1, index, gameState, distance+1);
            calculateDistances(x, y+1, index, gameState, distance+1);
            calculateDistances(x+1, y, index, gameState, distance+1);
        }

    }

    //get distances for x y
    public float[] getDistance(int x, int y){
        return distancesToSites[x][y];
    }
}
