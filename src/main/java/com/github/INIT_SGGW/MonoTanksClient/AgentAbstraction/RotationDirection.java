package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RotationDirection {
    LEFT(0),
    RIGHT(1);

    private final long value;

    @JsonValue
    public long toValue() {
        return value;
    }

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