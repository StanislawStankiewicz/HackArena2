package com.github.INIT_SGGW.MonoTanksBot.Bot;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public static Direction from(com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction direction) {
        return switch (direction) {
            case UP -> UP;
            case DOWN -> DOWN;
            case LEFT -> LEFT;
            case RIGHT -> RIGHT;
        };
    }

    public static com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction to(Direction direction) {
        return switch (direction) {
            case UP -> com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction.UP;
            case DOWN -> com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction.DOWN;
            case LEFT -> com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction.LEFT;
            case RIGHT -> com.github.INIT_SGGW.MonoTanksBot.websocket.packets.gameState.tile.Direction.RIGHT;
        };
    }

    public static Direction turn(Direction facingDirection, Direction turningDirection) {
        return switch (turningDirection) {
            case LEFT -> facingDirection.turnLeft();
            case RIGHT -> facingDirection.turnRight();
            default -> facingDirection;
        };
    }

    public Direction turnLeft() {
        return switch (this) {
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
        };
    }

    public Direction turnRight() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    public Direction turnAround() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}