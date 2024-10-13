package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

public enum AbilityType {
    FIRE_BULLET(0),
    FIRE_DOUBLE_BULLET(1),
    USE_LASER(2),
    USE_RADAR(3),
    DROP_MINE(4);

    private final int value;

    AbilityType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
