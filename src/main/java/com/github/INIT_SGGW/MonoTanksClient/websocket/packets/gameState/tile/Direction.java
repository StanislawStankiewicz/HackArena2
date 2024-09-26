package com.github.INIT_SGGW.MonoTanksClient.websocket.packets.gameState.tile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the direction in which a tank, turrent or bullet is facing.
 */
@Getter
@RequiredArgsConstructor
public enum Direction {
    /**
     * Represents the upward direction.
     */
    UP(0),

    /**
     * Represents the rightward direction.
     */
    RIGHT(1),

    /**
     * Represents the downward direction.
     */
    DOWN(2),

    /**
     * Represents the leftward direction.
     */
    LEFT(3);

    /**
     * The integer value associated with the direction.
     */
    private final int value;

    /**
     * Creates a Direction from an integer value.
     *
     * @param value the integer value representing the direction
     * @return the corresponding Direction
     * @throws IllegalArgumentException if the value does not correspond to any Direction
     */
    @JsonCreator
    public static Direction fromValue(int value) {
        for (Direction direction : Direction.values()) {
            if (direction.value == value) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    /**
     * Converts the Direction to its integer value.
     *
     * @return the integer value representing the direction
     */
    @JsonValue
    public int toValue() {
        return value;
    }
}