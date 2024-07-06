package com.chess.engine.pieces;

public enum PieceType {

    PAWN(1, "P"), KNIGHT(3, "N"), BISHOP(3, "B"), ROOK(5, "R"), QUEEN(9, "Q"), KING(-1, "K");

    private final int value;
    private final String pieceName;

    PieceType(final int value, final String pieceName) {
        this.value = value;
        this.pieceName = pieceName;
    }

    public int getPieceValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.pieceName;
    }
    
}