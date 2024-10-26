package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.BasicBoard;
import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Site;
import com.github.INIT_SGGW.MonoTanksBot.Bot.entities.Tank;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public class Board {
    List<Tank> tanks;
    List<Site> sites;
    BasicBoard<List<Entity>> tableOfEntities;

    public int getWidth() {
        return tableOfEntities.getWidth();
    }

    public int getHeight() {
        return tableOfEntities.getHeight();
    }

    public Board(GameState gameState) {
        
    }
}
