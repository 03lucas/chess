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

public class Bishop extends Piece{

    public Bishop(final Position piecePosition, final Color color, final boolean isFirstMove) {
        super(piecePosition, PieceType.BISHOP, color, isFirstMove);
    }

    @Override
    public Collection<Move> getPossibleMoves(final Board board) {
        List<Move> pieceMoves = new ArrayList<>();

        int[] dx = {1, -1};
        int[] dy = {1, -1};

        Position start = this.getPiecePosition();

        for (int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++){
                int newX = start.getXCoord() + dx[j];
                int newY = start.getYCoord() + dy[i];
                Position pos = new Position(newX, newY);

                while (BoardUtils.isValidTileCoord(pos)) {
                    Tile destTile = board.getTileByPos(pos);
                    if (!destTile.isTileOccupied()) {
                        Move move = new MajorMove(board, this, pos);
                        pieceMoves.add(move);
                    } else {
                        if (destTile.getPieceOnTile().getPieceColor() != this.getPieceColor()) {
                            Move move = new MajorAttackMove(board, this, pos, destTile.getPieceOnTile());
                            pieceMoves.add(move);
                        }
                        break;
                    }
                    newX += dx[j];
                    newY += dy[i];
                    pos = new Position(newX, newY);
                }
            }

            
        }

        return ImmutableList.copyOf(pieceMoves);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor(), false);
    }

}
