package com.github.INIT_SGGW.MonoTanksBot.Bot.entities;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Board;

import java.util.Map;

public class DistanceTable {
    BasicBoard<Map<Site, Float>> distances;

    public DistanceTable(Board board) {
        int width = board.getWidth();
        int height = board.getHeight();
        distances = new BasicBoard<>(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distances.set(x, y, new java.util.HashMap<>());
            }
        }
    }
}
