package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Color;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.PieceType;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

public class BlackPlayer extends Player {

    public BlackPlayer(final Board board, final Collection<Move> blackStandardLegalMoves, final Collection<Move> whiteStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {
        
        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()) {

            //verifica se tem peças entre o rei e a torre
            if(!this.board.getTile(0, 5).isTileOccupied() &&
                !this.board.getTile(0, 6).isTileOccupied()) {
                
                final Piece kingSideRook = this.board.getTile(0, 7).getPieceOnTile();
                
                if(kingSideRook != null && kingSideRook.isFirstMove()) {

                    //!!!!SE DER NULLPOINTER EXCEPTION, TROCAR board.getTile().getPosition() POR new Position()
                    //verifica se as duas posicoes entre o rei e a torre estao sendo atacadas
                    if(Player.calculateAttacksOnTile(board.getTile(0, 5).getPosition(), opponentsLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(board.getTile(0, 6).getPosition(), opponentsLegals).isEmpty() &&
                       kingSideRook.getPieceType() == PieceType.ROOK) {
                        
                        kingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing, 
                                                                    board.getTile(0, 6).getPosition(), 
                                                                    (Rook) kingSideRook, 
                                                                    board.getTile(0, 5).getPosition()));
                    
                    }
                    
                }
            }

            //verifica se tem peças entre o rei e a torre
            if(!this.board.getTile(0, 1).isTileOccupied() && !this.board.getTile(0, 2).isTileOccupied() && !this.board.getTile(7, 3).isTileOccupied()) {
                
                final Piece queenSideRook = this.board.getTile(0, 0).getPieceOnTile();
                
                if(queenSideRook != null && queenSideRook.isFirstMove()) {

                    //!!!!SE DER NULLPOINTER EXCEPTION, TROCAR board.getTile().getPosition() POR new Position()
                    //verifica se as tres posicoes entre o rei e a torre estao sendo atacadas
                    if(Player.calculateAttacksOnTile(board.getTile(0, 1).getPosition(), opponentsLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(board.getTile(0, 2).getPosition(), opponentsLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(board.getTile(0, 3).getPosition(), opponentsLegals).isEmpty() &&
                       queenSideRook.getPieceType() == PieceType.ROOK) {
                        
                        kingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing, 
                                                                    board.getTile(0, 2).getPosition(), 
                                                                    (Rook) queenSideRook, 
                                                                    board.getTile(0, 3).getPosition()));
                    
                    }
                    
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }

}
