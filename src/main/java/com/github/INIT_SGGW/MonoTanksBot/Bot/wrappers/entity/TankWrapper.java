package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity;

import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.DirectionWrapper;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.MoveDirection;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.RotationDirection;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Turret;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import static com.github.INIT_SGGW.MonoTanksBot.Bot.Config.BULLET_SPEED;

public class TankWrapper extends EntityWrapper {

    private Tile.Tank tank;

    public TankWrapper(Tile.Tank tank, int x, int y) {
        super(x, y);
        this.tank = tank;
    }

    public String getId() {
        return tank.getOwnerId();
    }

    public DirectionWrapper getBodyDirection() {
        return DirectionWrapper.from(tank.getDirection());
    }

    private void setBodyDirection(DirectionWrapper direction) {
        tank.setDirection(DirectionWrapper.to(direction));
    }

    public void rotateBody(RotationDirection direction) {
        setBodyDirection(DirectionWrapper.turn(getBodyDirection(), direction));
    }

    public DirectionWrapper getTurretDirection() {
        return DirectionWrapper.from(tank.getTurret().direction());
    }

    private void setTurretDirection(DirectionWrapper direction) {
        tank.setTurret(new Turret(
                DirectionWrapper.to(direction),
                tank.getTurret().bulletCount(),
                tank.getTurret().ticksToRegenBullet()));
    }

    public void rotateTurret(RotationDirection direction) {
        setTurretDirection(DirectionWrapper.turn(getBodyDirection(), direction));
    }

    public ItemType getSpecialItem() {
        return tank.getSecondaryItem().orElse(ItemType.UNKNOWN);
    }

    public void setSpecialItem(ItemType item) {
        tank.setSecondaryItem(Optional.of(item));
    }

    public long getBulletsCount() {
        return tank.getTurret().bulletCount().orElse(0L);
    }

    public void move(MoveDirection direction) {
        int[] coords = getCoordsInDirection(getTurretDirection());

        if (direction == MoveDirection.BACKWARD) {
            coords[0] *= -1;
            coords[1] *= -1;
        }
        moveTo(coords[0], coords[1]);
    }

    public BulletWrapper shoot(Tile.BulletType type) {
        if (type == Tile.BulletType.DOUBLE) {
            assert tank.getSecondaryItem().isPresent() && tank.getSecondaryItem().get() == ItemType.DOUBLE_BULLET;
        } else {
            assert getBulletsCount() > 0;
        }

        if (type == Tile.BulletType.DOUBLE) {
            tank.setSecondaryItem(Optional.empty());
        }
        tank.setTurret(
                new Turret(
                        tank.getTurret().direction(),
                        tank.getTurret().bulletCount().map(count -> count - 1),
                        tank.getTurret().ticksToRegenBullet()));

        int[] coords = getCoordsInDirection(getTurretDirection());

        return new BulletWrapper(
                coords[0],
                coords[1],
                tank.getTurret().direction(),
                BULLET_SPEED,
                type);
    }

    public LaserWrapper shootLaser() {
        int[] coords = getCoordsInDirection(getTurretDirection());
        return new LaserWrapper(getTurretDirection(), coords[0], coords[1]);
    }

    private int[] getCoordsInDirection(DirectionWrapper direction) {
        int x = this.x;
        int y = this.y;

        switch (direction) {
            case UP:
                x--;
                break;
            case DOWN:
                x++;
                break;
            case LEFT:
                y--;
                break;
            case RIGHT:
                y++;
                break;
        }

        return new int[] {x, y};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TankWrapper that))
            return false;
        return equals(that);
    }

    public boolean equals(TankWrapper that) {
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
    
    public TankWrapper clone() {
        Tile.Tank newTank = new Tile.Tank();
        newTank.setOwnerId(tank.getOwnerId());
        newTank.setDirection(tank.getDirection());
        newTank.setHealth(Optional.of(tank.getHealth().orElse(0L)));
        Turret newTurret = new Turret(
                tank.getTurret().direction(),
                Optional.of(tank.getTurret().bulletCount().orElse(0L)),
                Optional.of(tank.getTurret().ticksToRegenBullet().orElse(0d))
        );
        newTank.setTurret(newTurret);
        newTank.setSecondaryItem(tank.getSecondaryItem());
        return new TankWrapper(tank, x, y);
    }

    @Override
    public String toString() {
        return "TankWrapper{" +
                "tank=" + tank +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
