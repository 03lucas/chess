package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.Color;
import com.google.common.collect.ImmutableList;

public class Knight extends Piece{

    public Knight(final Position piecePosition, final Color color, final boolean isFirstMove) {
        super(piecePosition, PieceType.KNIGHT, color, isFirstMove);
    }

    @Override
    public Collection<Move> getPossibleMoves(final Board board) {
        List<Move> pieceMoves = new ArrayList<>();

        int[] dx = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

        Position start = this.getPiecePosition();
        
        for (int i = 0; i < 8; i++) {
            int newX = start.getXCoord() + dx[i];
            int newY = start.getYCoord() + dy[i];
            Position pos = new Position(newX, newY);
            
            if (BoardUtils.isValidTileCoord(pos)) {
                Tile destTile = board.getTileByPos(pos);
                
                if (!destTile.isTileOccupied()) {
                    Move move = new MajorMove(board, this, pos);
                    pieceMoves.add(move);
                } else {
                    if (destTile.getPieceOnTile().getPieceColor() != this.getPieceColor()) {
                        Move move = new MajorAttackMove(board, this, pos, destTile.getPieceOnTile());
                        pieceMoves.add(move);
                    }
                    
                }
            }
        }

        return ImmutableList.copyOf(pieceMoves);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor(), false);
    }

}
