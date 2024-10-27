package com.github.INIT_SGGW.MonoTanksBot.Bot.wrappers;

import com.github.INIT_SGGW.MonoTanksBot.BotAbstraction.RotationDirection;
import com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction;

public enum DirectionWrapper {
    UP, DOWN, LEFT, RIGHT;

    public static DirectionWrapper from(Direction direction) {
        return switch (direction) {
            case UP -> UP;
            case DOWN -> DOWN;
            case LEFT -> LEFT;
            case RIGHT -> RIGHT;
        };
    }

    public static Direction to(DirectionWrapper direction) {
        return switch (direction) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
        };
    }

    public static DirectionWrapper turn(DirectionWrapper facingDirection, RotationDirection rotationDirection) {
        return switch (rotationDirection) {
            case LEFT -> facingDirection.turnLeft();
            case RIGHT -> facingDirection.turnRight();
        };
    }

    public DirectionWrapper turnLeft() {
        return switch (this) {
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
        };
    }

    public DirectionWrapper turnRight() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    public DirectionWrapper turnAround() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}