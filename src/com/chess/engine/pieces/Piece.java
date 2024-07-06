package com.chess.engine.pieces;

import java.util.Collection;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Position;
import com.chess.engine.Color;

public abstract class Piece {

    protected final Position piecePosition;
    protected final PieceType pieceType;
    private final int cachedHashCode; //para não ser necessario calcular o hashcode toda vez
    protected final Color color;//é utilizado enum por ser TypeSafe (se utilizado um int para definir a cor, qualquer numero inteiro pode ser passado)
    protected final boolean isFirstMove;
    

    Piece(final Position piecePosition, final PieceType pieceType, final Color color, final boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.pieceType = pieceType;
        this.color = color;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        int result = piecePosition.hashCode();
        result = 31 * result + pieceType.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    //pega a posicao da peca
    public Position getPiecePosition() {
        return this.piecePosition;
    }

    public PieceType getPieceType(){
        return this.pieceType;
    }

    public boolean isFirstMove(){
        return this.isFirstMove;
    }

    public Color getPieceColor(){
        return this.color;
    }

    //metodo para cada peça implementar sua propria logica de movimento
    public abstract Collection<Move> getPossibleMoves(final Board board);

    public abstract Piece movePiece(Move move);

    //obj equality não reference equality
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Piece)) {
            return false;
        }
        final Piece piece = (Piece) obj;
        return piecePosition == piece.getPiecePosition() && pieceType == piece.getPieceType() &&
               color == piece.getPieceColor() && isFirstMove == piece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

}
