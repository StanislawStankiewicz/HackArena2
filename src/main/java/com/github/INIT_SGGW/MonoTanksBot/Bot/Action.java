package com.github.INIT_SGGW.MonoTanksBot.Bot;

import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.GameStateWrapper;
import com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity.TankWrapper;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.RotationDirection;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;

public enum Action {
    MOVE_FORWARD,
    MOVE_BACKWARD,
    ROTATE_TANK_LEFT,
    ROTATE_TANK_RIGHT,
    ROTATE_TURRET_LEFT,
    ROTATE_TURRET_RIGHT,
    USE_FIRE_BULLET,
    USE_FIRE_DOUBLE_BULLET,
    USE_LASER,
    USE_RADAR,
    DROP_MINE,
    PASS;

    public void perform(GameStateWrapper gameState, TankWrapper tank) {
        switch (this) {
            case MOVE_FORWARD -> moveForward(gameState, tank);
            case MOVE_BACKWARD -> moveBackward(gameState, tank);
            case ROTATE_TANK_LEFT -> rotateTankLeft(gameState, tank);
            case ROTATE_TANK_RIGHT -> rotateTankRight(gameState, tank);
            case ROTATE_TURRET_LEFT -> rotateTurretLeft(gameState, tank);
            case ROTATE_TURRET_RIGHT -> rotateTurretRight(gameState, tank);
            case USE_FIRE_BULLET -> useFireBullet(gameState, tank);
            case USE_FIRE_DOUBLE_BULLET -> useFireDoubleBullet(gameState, tank);
            case USE_LASER -> useLaser(gameState, tank);
            case USE_RADAR -> useRadar(gameState, tank);
            case DROP_MINE -> dropMine(gameState, tank);
            case PASS -> pass(gameState, tank);
        }
    }

    private void moveForward(GameStateWrapper gameState, TankWrapper tank) {
        int x = tank.getX();
        int y = tank.getY();
        switch (tank.getBodyDirection()) {
            case UP -> gameState.moveEntity(tank, x - 1, y);
            case DOWN -> gameState.moveEntity(tank, x + 1, y);
            case LEFT -> gameState.moveEntity(tank, x, y - 1);
            case RIGHT -> gameState.moveEntity(tank, x, y + 1);
        }
    }

    private void moveBackward(GameStateWrapper gameState, TankWrapper tank) {
        int x = tank.getX();
        int y = tank.getY();
        switch (tank.getBodyDirection()) {
            case UP -> gameState.moveEntity(tank, x + 1, y);
            case DOWN -> gameState.moveEntity(tank, x - 1, y);
            case LEFT -> gameState.moveEntity(tank, x, y + 1);
            case RIGHT -> gameState.moveEntity(tank, x, y - 1);
        }
    }

    private void rotateTankLeft(GameStateWrapper gameState, TankWrapper tank) {
        tank.rotateBody(RotationDirection.LEFT);
    }

    private void rotateTankRight(GameStateWrapper gameState, TankWrapper tank) {
        tank.rotateBody(RotationDirection.RIGHT);
    }

    private void rotateTurretLeft(GameStateWrapper gameState, TankWrapper tank) {
        tank.rotateTurret(RotationDirection.LEFT);
    }

    private void rotateTurretRight(GameStateWrapper gameState, TankWrapper tank) {
        tank.rotateTurret(RotationDirection.RIGHT);
    }

    private void useFireBullet(GameStateWrapper gameState, TankWrapper tank) {
        assert tank.getBulletsCount() > 0;

        gameState.createBullet(tank.shoot(Tile.BulletType.BASIC));
    }

    private void useFireDoubleBullet(GameStateWrapper gameState, TankWrapper tank) {
        gameState.createBullet(tank.shoot(Tile.BulletType.DOUBLE));
    }

    private void useLaser(GameStateWrapper gameState, TankWrapper tank) {
        gameState.createLasers(tank.shootLaser());
    }

    private void useRadar(GameStateWrapper gameState, TankWrapper tank) {
        // no way to simulate (DING DING DING +10000000 points!!)
    }

    private void dropMine(GameStateWrapper gameState, TankWrapper tank) {
        gameState.createMine(tank.getX(), tank.getY());
    }

    private void pass(GameStateWrapper gameState, TankWrapper tank) {
        // pass
    }
}
