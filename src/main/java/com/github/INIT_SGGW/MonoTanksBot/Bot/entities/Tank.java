package com.github.INIT_SGGW.MonoTanksBot.Bot.entities;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Direction;
import com.github.INIT_SGGW.MonoTanksBot.Bot.MoveType;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.ItemType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Tank {
    private String id;
    private int x;
    private int y;
    private Direction drivingDirection;
    private Direction shootingDirection;
    private ItemType specialItem;
    private boolean isEnemy;
    private int bulletsAmount;

    public Tank(String id, int x, int y, Direction drivingDirection, Direction shootingDirection, boolean isEnemy, int bulletsAmount) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.drivingDirection = drivingDirection;
        this.shootingDirection = shootingDirection;
        specialItem=ItemType.UNKNOWN;
        this.isEnemy = isEnemy;
        this.bulletsAmount = bulletsAmount;
    }

    public int[] getXYInFront() {
        int x = this.x;
        int y = this.y;

        switch (drivingDirection) {
            case Direction.UP -> {
                --x;
            }
            case Direction.DOWN -> {
                ++x;
            }
            case Direction.LEFT -> {
                --y;
            }
            case Direction.RIGHT -> {
                ++y;
            }
        }

        return new int[]{x, y};
    }

    public int[] getXYBehind() {
        int x = this.x;
        int y = this.y;

        switch (drivingDirection) {
            case Direction.UP -> {
                ++x;
            }
            case Direction.DOWN -> {
                --x;
            }
            case Direction.LEFT -> {
                ++y;
            }
            case Direction.RIGHT -> {
                --y;
            }
        }

        return new int[]{x, y};
    }

}
