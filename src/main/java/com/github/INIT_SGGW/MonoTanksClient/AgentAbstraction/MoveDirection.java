package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the direction of tank movement.
 */
@Getter
@RequiredArgsConstructor
public enum MoveDirection {
    /**
     * Represents a forward movement direction.
     */
    FORWARD(0),

    /**
     * Represents a backward movement direction.
     */
    BACKWARD(1);

    /**
     * The integer value associated with the movement direction.
     */
    private final int value;

    /**
     * Converts the MoveDirection to its integer value.
     *
     * @return the integer value representing the movement direction
     */
    @JsonValue
    public int toValue() {
        return value;
    }

    /**
     * Creates a MoveDirection from an integer value.
     *
     * @param value the integer value representing the movement direction
     * @return the corresponding MoveDirection
     * @throws IllegalArgumentException if the value does not correspond to any MoveDirection
     */
    @JsonCreator
    public static MoveDirection fromValue(int value) {
        for (MoveDirection direction : MoveDirection.values()) {
            if (direction.getValue() == value) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}