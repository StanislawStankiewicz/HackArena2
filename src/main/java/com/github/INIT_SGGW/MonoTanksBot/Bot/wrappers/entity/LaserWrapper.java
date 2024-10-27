package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity;

import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.DirectionWrapper;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import lombok.Getter;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.Config.LASER_DURATION;

public class LaserWrapper extends EntityWrapper {

    private Tile.Laser laser;
    private int ticksRemaining;
    @Getter
    private DirectionWrapper direction;

    public LaserWrapper(Tile.Laser laser, int x, int y) {
        super(x, y);
        this.laser = laser;
        this.ticksRemaining = LASER_DURATION;
    }

    public LaserWrapper(DirectionWrapper direction, int x, int y) {
        super(x, y);
        this.direction = direction;
    }

    public LaserWrapper clone(){
        Tile.Laser laser = new Tile.Laser();
        laser.setId(this.laser.getId());
        laser.setOrientation(this.laser.getOrientation());
        return new LaserWrapper(laser, x, y);
    }
}
