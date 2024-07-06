package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Move.PawnAttackMove;
import com.chess.engine.board.Move.PawnEnPassantAttackMove;
import com.chess.engine.board.Move.PawnJump;
import com.chess.engine.board.Move.PawnMove;
import com.chess.engine.board.Move.PawnPromotion;
import com.chess.engine.Color;
import com.google.common.collect.ImmutableList;

public class Pawn extends Piece {

    public Pawn(final Position piecePosition, final Color color, final boolean isFirstMove) {
        super(piecePosition, PieceType.PAWN, color, isFirstMove);
    }

    @Override
    public Collection<Move> getPossibleMoves(final Board board) {
        List<Move> pieceMoves = new ArrayList<>();

        Position start = this.getPiecePosition();
        int direction = color.getDirection();
        Tile destTile = null;

        //Movimento para frente (1 casa)
        Position pos = new Position(start.getXCoord() + direction, start.getYCoord());
        if (BoardUtils.isValidTileCoord(pos)) {
            destTile = board.getTileByPos(pos);
            if (!destTile.isTileOccupied()) {//se a casa da frente esta vazia
                if(this.color.isPawnPromotionSquare(pos)) {//se a peca chegou na ultima casa do tabuleiro e um movimento de promocao
                    pieceMoves.add(new PawnPromotion(new PawnMove(board, this, pos)));
                } else {//se não chegou na ultima casa do tabuleiro e um movimento normal
                    pieceMoves.add(new PawnMove(board, this, pos));
                }
            }
        }

        //Movimento inicial (2 casas)
        pos = new Position(start.getXCoord() + (2 * direction), start.getYCoord());
        if (this.isFirstMove() && BoardUtils.isValidTileCoord(pos)) {
            destTile = board.getTileByPos(pos);
            if (!destTile.isTileOccupied() && board.getTileByPos(new Position(start.getXCoord() + direction, start.getYCoord())).getPieceOnTile() == null){
                pieceMoves.add(new PawnJump(board, this, pos));
            }
        }

        //Captura diagonal e enPassant
        int[] dx = {1, -1};
        for (int i = 0; i < 2; i++) {
            pos = new Position(start.getXCoord() + direction, start.getYCoord() + dx[i]);
            if (BoardUtils.isValidTileCoord(pos)) {
                destTile = board.getTileByPos(pos);
                if (destTile.isTileOccupied() && destTile.getPieceOnTile().getPieceColor() != this.color){//diagonal
                    if(this.color.isPawnPromotionSquare(pos)) {//se a peca atacou e chegou na ultima casa do tabuleiro promove
                        pieceMoves.add(new PawnPromotion(new PawnAttackMove(board, this, pos, destTile.getPieceOnTile())));
                    } else {//se não chegou na ultima casa do tabuleiro é um ataque normal
                        pieceMoves.add(new PawnAttackMove(board, this, pos, destTile.getPieceOnTile()));
                    }
                } else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPieceColor() != this.color) {//enPassant
                    //verifica se o ataque do peao atual esta em cima da casa anterior ao peao que fez enPassant
                    Position enPassantPos = new Position(board.getEnPassantPawn().getPiecePosition().getXCoord() + direction, board.getEnPassantPawn().getPiecePosition().getYCoord());
                    if (pos.equals(enPassantPos)) {
                        pieceMoves.add(new PawnEnPassantAttackMove(board, this, pos, board.getEnPassantPawn()));
                    }
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
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor(), false);
    }

    public Piece getPromotionPiece() {
        return new Queen(this.piecePosition, this.color, false);
    }

}