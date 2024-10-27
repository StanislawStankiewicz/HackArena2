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

    public Status getStatus() {
        return switch (zone.getStatus()) {
            case Zone.ZoneStatus.Neutral neutral -> Status.NEUTRAL;
            case Zone.ZoneStatus.BeingCaptured beingCaptured -> Status.BEING_CAPTURED;
            case Zone.ZoneStatus.Captured captured -> Status.CAPTURED;
            case Zone.ZoneStatus.BeingContested beingContested -> Status.BEING_CONTESTED;
            case null, default -> throw new IllegalStateException("Unknown zone status");
        };
    }

    public OwnedBy owner() {
        return switch (zone.getStatus()) {
            case Zone.ZoneStatus.Neutral neutral -> OwnedBy.NOONE;
            case Zone.ZoneStatus.BeingCaptured beingCaptured -> OwnedBy.ENEMY;
            case Zone.ZoneStatus.Captured captured -> OwnedBy.US;
            case Zone.ZoneStatus.BeingContested beingContested -> OwnedBy.NOONE; // TODO: check if it should be noone
            case null, default -> throw new IllegalStateException("Unknown zone status");
        };
    }

    enum Status {
        NEUTRAL, BEING_CAPTURED, CAPTURED, BEING_CONTESTED
    }
    enum OwnedBy {
        NOONE, US, ENEMY
    }
}