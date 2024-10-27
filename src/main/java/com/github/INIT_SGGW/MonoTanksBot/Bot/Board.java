package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.*;
import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity.TankWrapper;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;

import java.util.List;

public class Board {

    private GameStateWrapper gameState;

    public boolean isWall(int x, int y) {
        return gameState.isWall(x, y);
    }

    public List<ZoneWrapper> getZones() {
        return gameState.getZones();
    }

    public int getWidth() {
        return gameState.getTableOfEntities()
                .getWidth();
    }

    public int getHeight() {
        return gameState.getTableOfEntities()
                .getHeight();
    }

    public Board(GameState gameState) {
        this.gameState = new GameStateWrapper(gameState);
    }

    public void perform(Action action, TankWrapper tank) {
        gameState.perform(action, tank);
    }
}
