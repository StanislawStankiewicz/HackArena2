package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EntityWrapper {
    protected int x;
    protected int y;

    public EntityWrapper(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void moveTo(int x, int y) {
        assert Math.abs(this.x - x) + Math.abs(this.y - y) <= 1;

        this.x = x;
        this.y = y;
    }
}
