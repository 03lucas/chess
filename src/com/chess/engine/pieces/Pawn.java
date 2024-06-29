package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.List;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.Color;
import com.google.common.collect.ImmutableList;

public class Pawn extends Piece {

    public Pawn(Position piecePosition, Color color) {
        super(piecePosition, PieceType.PAWN, color);
    }

    @Override
    public List<Move> getPossibleMoves(final Board board) {
        List<Move> pieceMoves = new ArrayList<>();

        Position start = this.getPiecePosition();
        int direction = (color == Color.WHITE) ? -1 : 1;

        // Movimento para frente (1 casa)
        Position pos = new Position(start.getXCoord() + direction, start.getYCoord());

        if (BoardUtils.isValidTileCoord(pos)) {
            Tile destTile = board.getTileByPos(pos);
            if (destTile.getPieceOnTile() == null) {
                pieceMoves.add(new MajorMove(board, this, pos));
            }
        }

        // Movimento inicial (2 casas)
        pos = new Position(start.getXCoord() + (2 * direction), start.getYCoord());

        if (this.isFirstMove() && BoardUtils.isValidTileCoord(pos)) {
            
            Tile destTile = board.getTile(pos.getXCoord(), pos.getYCoord());
            if (destTile.getPieceOnTile() == null) {
                pieceMoves.add(new MajorMove(board, this, pos));
            }
        }

        // Captura diagonal
        int[] dx = {1, -1};
        for (int i = 0; i < 2; i++) {
            pos = new Position(start.getXCoord() + direction, start.getYCoord() + dx[i]);
            if (BoardUtils.isValidTileCoord(pos)) {
                Tile destTile = board.getTile(pos.getXCoord(), pos.getYCoord());
                if (destTile.getPieceOnTile() != null && destTile.getPieceOnTile().getPieceType() != this.getPieceType()) {
                    pieceMoves.add(new MajorMove(board, this, pos));
                }
            }
        }
        
        return ImmutableList.copyOf(pieceMoves);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor());
    }

}