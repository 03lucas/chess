package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.List;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Position;
import com.chess.engine.Color;
import com.google.common.collect.ImmutableList;

public class King extends Piece {
    private boolean castlingDone;

    public King(final Position piecePosition, final Color color) {
        super(piecePosition, PieceType.KING, color);
        this.castlingDone = false;
    }

    @Override
    public List<Move> getPossibleMoves(final Board board) {
        List<Move> pieceMoves = new ArrayList<>();

        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        Position start = this.getPiecePosition();
        
        for (int i = 0; i < 8; i++) {
            int newX = start.getXCoord() + dx[i];
            int newY = start.getYCoord() + dy[i];
            Position pos = new Position(newX, newY);
            if (BoardUtils.isValidTileCoord(pos)) {
                Move move = new MajorMove(board, this, pos);
                pieceMoves.add(move);
            }
        }

        return ImmutableList.copyOf(pieceMoves);
    }

    public boolean isCastlingDone() {
        return this.castlingDone;
    }
    
    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    @Override
    public King movePiece(final Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor());
    }

}