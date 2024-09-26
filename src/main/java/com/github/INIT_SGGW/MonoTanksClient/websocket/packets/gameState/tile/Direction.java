package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Direction {
    UP(0),
    RIGHT(1),
    DOWN(2),
    LEFT(3);

    private final int value;

    @JsonCreator
    public static Direction fromValue(int value) {
        for (Direction direction : Direction.values()) {
            if (direction.value == value) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    @JsonValue
    public int toValue() {
        return value;
    }
}