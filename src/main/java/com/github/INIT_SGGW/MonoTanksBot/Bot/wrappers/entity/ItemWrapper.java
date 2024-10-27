package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity;

import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

public class ItemWrapper extends EntityWrapper {
    private Tile.Item item;

    public ItemType getItemType() {
        return item.getItemType();
    }

    public ItemWrapper(Tile.Item item, int x, int y) {
        super(x, y);
        this.item = item;
    }
}
