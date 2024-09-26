package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the direction of rotation.
 */
@Getter
@RequiredArgsConstructor
public enum RotationDirection {
    /**
     * Represents a left rotation direction.
     */
    LEFT(0),

    /**
     * Represents a right rotation direction.
     */
    RIGHT(1);

    /**
     * The integer value associated with the rotation direction.
     */
    private final long value;

    /**
     * Converts the RotationDirection to its integer value.
     *
     * @return the integer value representing the rotation direction
     */
    @JsonValue
    public long toValue() {
        return value;
    }

    /**
     * Creates a RotationDirection from an integer value.
     *
     * @param value the integer value representing the rotation direction
     * @return the corresponding RotationDirection
     * @throws IllegalArgumentException if the value does not correspond to any RotationDirection
     */
    @JsonCreator
    public static RotationDirection fromValue(long value) {
        for (RotationDirection rotationDirection : RotationDirection.values()) {
            if (rotationDirection.getValue() == value) {
                return rotationDirection;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}