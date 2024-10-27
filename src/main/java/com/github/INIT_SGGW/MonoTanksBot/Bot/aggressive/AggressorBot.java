package com.github.INIT_SGGW.MonoTanksBot.Bot.aggressive;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Action;
import com.github.INIT_SGGW.MonoTanksBot.Bot.MoveMap;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.Bot;
import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.BotResponse;
import com.github.INIT_SGGW.MonoTanksBot.websocket.Warning;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameEnd.GameEnd;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.GameState;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Tile;
import lombok.AllArgsConstructor;


public class AggressorBot{

    public long tick;
    public String id;
    public Point targetPoint;
    public GameStateUtils utils;

    public State state;

    public AggressorBot(String id) {
        this.id = id;
        this.state = State.WANDERING;
        this.tick = 0;
    }

    public Action nextMove(GameState gameState) {
        this.tick++;
        utils = new GameStateUtils(gameState);
        state = utils.determineState(id);
        if (state == State.WANDERING) {
            return wander();
        }
        if (state == State.APPROACHING) {
            return approach();
        }
        if (state == State.ATTACKING) {
            return attack();
        }
        return null;
    }

    private Action attack() {
        // Get our tank
        Tile.Tank ourTank = utils.findTankById(id);
        if (ourTank == null) {
            return Action.PASS;
        }

        // Get the closest enemy position
        Point enemyPosition = utils.getClosestEnemyPosition(id);
        if (enemyPosition == null) {
            // No visible enemies; default to wandering
            state = State.WANDERING;
            return wander();
        }

        // Get our tank's position
        Point ourPosition = utils.getTankPosition(id);

        // Keep the turret rotated towards the enemy
        Action turretAction = utils.getTurretRotationAction(ourTank, ourPosition, enemyPosition);
        if (turretAction != Action.PASS) {
            return turretAction;
        }

        // Check if we are aligned with the enemy
        if (utils.isAligned(ourPosition, enemyPosition)) {
            // Fire if we have bullets
            if (ourTank.getTurret().bulletCount().orElse(0L) > 0) {
                return Action.USE_FIRE_BULLET;
            } else {
                // No bullets; consider reloading or other action
                return Action.PASS;
            }
        } else {
            // Move to get aligned with the enemy
            return utils.getMoveToAlignWithEnemy(id, enemyPosition);
        }
    }

    private Action wander() {
        if (tick % 10 == 0) {
            return Action.ROTATE_TURRET_LEFT;
        }
        if (utils.getTankPosition(id).equals(targetPoint)) {
            targetPoint = null;
        }
        if (targetPoint == null) {
            targetPoint = utils.getRandomPoint(id);
            System.out.println("New target point: " + targetPoint);
        }
        return utils.getMoveToGetToPoint(id, targetPoint);
    }

    private Action approach() {
        return utils.getMoveToGetToPoint(id, utils.getClosestEnemyPosition(id));
    }

    enum State {
        WANDERING,
        APPROACHING,
        ATTACKING,
        CIRCLING
    }
}
