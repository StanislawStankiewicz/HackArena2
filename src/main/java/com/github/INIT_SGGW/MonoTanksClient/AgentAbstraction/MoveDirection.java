package com.github.INIT_SGGW.MonoTanksClient.AgentAbstraction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoveDirection {
    FORWARD(0),
    BACKWARD(1);

    private final int value;

    @JsonValue
    public int toValue() {
        return value;
    }

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