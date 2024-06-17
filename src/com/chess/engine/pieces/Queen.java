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

public class Queen extends Piece{

    public Queen(Position piecePosition, Color color) {
        super(piecePosition, PieceType.QUEEN, color);
    }

     @Override
     public List<Move> getPossibleMoves(final Board board) {
        List<Move> pieceMoves = new ArrayList<>();

        int[] dx = {1, 1, -1, -1, 1, -1, 0, 0};
        int[] dy = {1, -1, 1, -1, 0, 0, 1, -1};

        Position start = this.getPiecePosition();

        for (int i = 0; i < 8; i++) {
            int newX = start.getXCoord() + dx[i];
            int newY = start.getYCoord() + dy[i];
            Position pos = new Position(newX, newY);

            while (BoardUtils.isValidTileCoord(pos)) {
                Move move = new MajorMove(board, this, pos);
                pieceMoves.add(move);
                newX += dx[i];
                newY += dy[i];
                pos = new Position(newX, newY);
            }
        }

        return ImmutableList.copyOf(pieceMoves);
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
    }

    @Override
    public Queen movePiece(final Move move) {
        return new Queen(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor());
    }

}