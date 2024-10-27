package com.github.INIT_SGGW.MonoTanksBot;

import java.io.Serializable;

public class BasicBoard<T> implements Serializable {
    private T[][] board;

    public BasicBoard(int width, int height) {
        board = (T[][]) new Object[width][height];
    }

    public T get(int x, int y) {
        return board[x][y];
    }

    public void set(int x, int y, T value) {
        board[x][y] = value;
    }

    public int getWidth() {
        return board.length;
    }

    public int getHeight() {
        return board[0].length;
    }
}
