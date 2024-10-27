package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity;

import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import lombok.Getter;

@Getter
public class MineWrapper extends EntityWrapper {
    private Tile.Mine mine;

    public MineWrapper(Tile.Mine mine, int x, int y) {
        super(x, y);
        this.mine = mine;
    }
}
