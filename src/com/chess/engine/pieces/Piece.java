package com.chess.engine.pieces;

import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public abstract class Piece {
    protected final int piecePos;
    protected final Alliance pieceAlliance;

    Piece(final int piecePos, final Alliance pieceAlliance) {
        this.pieceAlliance = pieceAlliance;
        this.piecePos = piecePos;
    }

    public Alliance getPieceAlliance(){
        return this.pieceAlliance;
    }

    //Sera sobrescrevido por cada peça
    public abstract Collection<Move> calculateLegalMoves(final Board board);

}
