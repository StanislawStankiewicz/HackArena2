package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.BasicBoard;
import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.ZoneWrapper;

import java.util.Map;

public class DistanceTable {
    BasicBoard<Map<ZoneWrapper, Integer>> distances;

    public DistanceTable(Board board) {
        int width = board.getWidth();
        int height = board.getHeight();

        // create a hashmap filled board
        distances = new BasicBoard<>(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distances.set(x, y, new java.util.HashMap<>());
            }
        }

        //initialize the distances to infinity
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (ZoneWrapper zone : board.getZones()) {
                    distances.get(x, y).put(zone, Integer.MAX_VALUE);
                }
            }
        }

        //find site tiles, and recursivly calculate the distance avoiding walls
        for (ZoneWrapper zone : board.getZones()) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (zone.contains(x, y)) {
                        distances.get(x, y).put(zone, 0);
                        //calculate the distances on all 4 sides
                        calculateDistances(x - 1, y, zone, board, 1);
                        calculateDistances(x, y - 1, zone, board, 1);
                        calculateDistances(x, y + 1, zone, board, 1);
                        calculateDistances(x + 1, y, zone, board, 1);
                    }
                }
            }
        }


    }

    private void calculateDistances(int x, int y, ZoneWrapper zone, Board board, int distance) {
        //check if the tile is out of bounds
        if (x < 0 || x >= board.getHeight() || y < 0 || y >= board.getWidth()) {
            return;
        }
        //check if the tile is a wall or a zone
        if (zone.contains(x, y)
                || board.isWall(x, y)) {
            return; //if it is return
        }

        //check if the distance is smaller than the current distance
        if (distances.get(x, y).get(zone) > distance) {
            distances.get(x, y).put(zone, distance);

            //calculate the distances on all 4 sides
            calculateDistances(x - 1, y, zone, board, distance + 1);
            calculateDistances(x, y - 1, zone, board, distance + 1);
            calculateDistances(x, y + 1, zone, board, distance + 1);
            calculateDistances(x + 1, y, zone, board, distance + 1);
        }
    }
}

