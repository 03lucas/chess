package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Color;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Position;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.chess.engine.pieces.PieceType;
import com.chess.engine.pieces.Rook;

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegalMoves, final Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }
    
    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {
        
        final List<Move> kingCastles = new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()) {

            //verifica se tem peças entre o rei e a torre
            if(!this.board.getTile(7, 5).isTileOccupied() &&
                !this.board.getTile(7, 6).isTileOccupied()) {
                
                final Piece kingSideRook = this.board.getTile(7, 7).getPieceOnTile();
                
                if(kingSideRook != null && kingSideRook.isFirstMove()) {
                        
                    //!!!!SE DER NULLPOINTER EXCEPTION, TROCAR board.getTile().getPosition() POR new Position()
                    //verifica se as duas posicoes entre o rei e a torre estao sendo atacadas
                    if(Player.calculateAttacksOnTile(new Position(7, 5),
                                                        opponentsLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(new Position(7, 6), 
                                                        opponentsLegals).isEmpty() &&
                        kingSideRook.getPieceType() == PieceType.ROOK) {
                        
                        kingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing,
                        new Position(7, 6), (Rook) kingSideRook, new Position(7, 5)));
                    
                    }
                    
                }
            }

            //verifica se tem peças entre o rei e a torre
            if(!this.board.getTile(7, 1).isTileOccupied() &&
                !this.board.getTile(7, 2).isTileOccupied() && 
                !this.board.getTile(7, 3).isTileOccupied()) {
                
                final Piece queenSideRook = this.board.getTile(7, 0).getPieceOnTile();
                
                if(queenSideRook != null && queenSideRook.isFirstMove()) {

                    //!!!!SE DER NULLPOINTER EXCEPTION, TROCAR board.getTile().getPosition() POR new Position()
                    //verifica se as tres posicoes entre o rei e a torre estao sendo atacadas
                    if(Player.calculateAttacksOnTile(new Position(7, 1),
                                                                            opponentsLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(new Position(7, 2), 
                                                                            opponentsLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(new Position(7, 3), 
                                                                            opponentsLegals).isEmpty() &&
                       queenSideRook.getPieceType() == PieceType.ROOK) {
                        
                        kingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing,
                        new Position(7, 2), (Rook) queenSideRook, new Position(7, 3)));
                    
                    }
                    
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }

}
