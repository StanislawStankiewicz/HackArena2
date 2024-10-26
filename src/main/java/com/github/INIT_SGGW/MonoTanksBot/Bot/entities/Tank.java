package com.github.INIT_SGGW.MonoTanksBot.Bot.entities;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Direction;
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
}
