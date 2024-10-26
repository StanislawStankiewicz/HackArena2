package com.github.INIT_SGGW.MonoTanksBot.Bot.entities;

import com.github.INIT_SGGW.MonoTanksBot.Bot.Direction;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Tank {
    private int x;
    private int y;
    private Direction drivingDirection;
    private Direction shootingDirection;
    private boolean isEnemy;

    public Tank(int x, int y, Direction drivingDirection, Direction shootingDirection, boolean isEnemy) {
        this.x = x;
        this.y = y;
        this.drivingDirection = drivingDirection;
        this.shootingDirection = shootingDirection;
        this.isEnemy = isEnemy;
    }



}
