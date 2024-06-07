package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

public class Knight extends Piece{
    
    //Os 8 possiveis movimentos do cavalo sempre vão ser esses + a posição atual
    private final static int[] CANDIDATE_MOVE_COORDS = {-17, -15, -10, -6, 6, 10, 15, 17};

    Knight(final int piecePos, final Alliance pieceAlliance) {
        super(piecePos, pieceAlliance);
        
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        
        int candidateDestCoord;
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidate : CANDIDATE_MOVE_COORDS){
            candidateDestCoord = this.piecePos + currentCandidate;
            
            if(BoardUtils.isValidTileCoord(candidateDestCoord)){

                //Não pode ir para o lado; Pulo de no max 2 colunas
                if (Math.abs (((this.piecePos% 8) + 1) - ((candidateDestCoord % 8) + 1)) > 2){
                    continue;
                }

                final Tile candidateDestTile = board.getTile(candidateDestCoord);

                //Se nao esta ocupado
                if(!candidateDestTile.isTileOccupied()){
                    legalMoves.add(new Move());
                } else { //Se estiver, pega a cor e o nome da peça que esta ocupando
                    final Piece pieceAtDest = candidateDestTile.getPiece();
                    final Alliance pAlliance = pieceAtDest.getPieceAlliance();
                    
                    //Se for inimigo, coloca como movimento possivel
                    if(this.pieceAlliance != pAlliance){
                        legalMoves.add(new Move());
                    }
                }
                
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

}
