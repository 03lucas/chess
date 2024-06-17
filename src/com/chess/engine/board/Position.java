package com.chess.engine.board;

public class Position {
    private final int xCoord;
    private final int yCoord;

    public Position(final int xCoord, final int yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public int getXCoord() {
        return xCoord;
    }

    public int getYCoord() {
        return yCoord;
    }

    //dar override no hashcode e no equals para poder comparar posições
    @Override
    public int hashCode() {
        return 31 * xCoord + yCoord;
    }

    @Override
    public boolean equals(final Object other) {
        return this.hashCode() == other.hashCode();
    }
}
