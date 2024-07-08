package com.chess.engine.pieces;

public enum PieceType {

    PAWN(100, "P"), KNIGHT(300, "N"), BISHOP(300, "B"), ROOK(500, "R"), QUEEN(900, "Q"), KING(10000, "K");

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