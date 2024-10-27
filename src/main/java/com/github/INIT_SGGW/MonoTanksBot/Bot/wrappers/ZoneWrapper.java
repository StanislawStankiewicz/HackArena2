package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers;

import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Zone;

public class ZoneWrapper {
    private Zone zone;

    public int getIndex() {
        return zone.getIndex();
    }

    public ZoneWrapper(Zone zone) {
        this.zone = zone;
    }

    public boolean contains(int x, int y) {
        return x >= zone.getX()
                && x < zone.getX() + zone.getWidth()
                && y >= zone.getY()
                && y < zone.getY() + zone.getHeight();
    }
}
