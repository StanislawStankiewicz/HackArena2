package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers;

import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.Zone;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class ZoneWrapper implements Serializable {
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

    public int getX() {
        return (int) zone.getX();
    }

    public int getY() {
        return (int) zone.getY();
    }

    public enum Status {
        NEUTRAL, BEING_CAPTURED, CAPTURED, BEING_CONTESTED
    }
    public enum OwnedBy {
        NOONE, US, ENEMY
    }

    @Override
    public ZoneWrapper clone() {
        Zone newZone = new Zone();
        newZone.setIndex(zone.index);
        newZone.setStatus(zone.getStatus());
        newZone.setX(zone.getX());
        newZone.setY(zone.getY());
        newZone.setWidth(zone.getWidth());
        newZone.setHeight(zone.getHeight());
        return new ZoneWrapper(newZone);
    }
}
