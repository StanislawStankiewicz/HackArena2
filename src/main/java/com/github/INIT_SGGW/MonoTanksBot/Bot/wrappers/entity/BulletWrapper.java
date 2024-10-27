package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity;

import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.DirectionWrapper;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

import java.io.Serializable;

public class BulletWrapper extends EntityWrapper {

    private Tile.Bullet bullet;

    public BulletWrapper(Tile.Bullet bullet, int x, int y) {
        super(x, y);
        this.bullet = bullet;
    }

    public BulletWrapper(int x, int y, Direction direction, double speed, Tile.BulletType type) {
        this(createBullet(direction, speed, type), x, y);
    }

    public DirectionWrapper getDirection() {
        return DirectionWrapper.from(bullet.getDirection());
    }

    public void advance() {
        switch (bullet.getDirection()) {
            case UP -> x++;
            case DOWN -> x--;
            case LEFT -> y--;
            case RIGHT -> y++;
        }
    }

    @Override
    protected void moveTo(int x, int y) {
        assert Math.abs(this.x - x) + Math.abs(this.y - y) <= 2;

        this.x = x;
        this.y = y;
    }

    private static Tile.Bullet createBullet(Direction direction, double speed, Tile.BulletType type) {
        Tile.Bullet bullet = new Tile.Bullet();
        bullet.setDirection(direction);
        bullet.setId(System.nanoTime());
        bullet.setSpeed(speed);
        bullet.setType(type);
        return bullet;
    }

    public BulletWrapper clone(){
        Tile.Bullet bullet = new Tile.Bullet();
        bullet.setDirection(this.bullet.getDirection());
        return new BulletWrapper(bullet, x, y);
    }
}
