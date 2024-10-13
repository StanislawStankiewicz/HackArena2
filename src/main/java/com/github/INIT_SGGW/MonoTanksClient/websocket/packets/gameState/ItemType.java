package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemType {
    UNKNOWN(0),
    LASER(1),
    DOUBLE_BULLET(2),
    RADAR(3),
    MINE(4);

    private final int value;

    ItemType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static ItemType fromValue(int value) {
        for (ItemType item : ItemType.values()) {
            if (item.value == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("Unknown SecondaryItem value: " + value);
    }
}
