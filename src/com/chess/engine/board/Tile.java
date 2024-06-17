package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public class Tile {

    private final Position position;
    private Piece pieceOnTile;

    private Tile(final Position position) 
    { 
        this.position = position;
    }

    public Piece getPieceOnTile() {
        return this.pieceOnTile;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPieceOnTile(final Piece pieceOnTile) {
        this.pieceOnTile = pieceOnTile;
    }

    public static Tile getInstance (final Position position) {
        return new Tile(position);
    }

    public boolean isTileOccupied() {
        return this.pieceOnTile != null;
    }
    

    //verifica se o Tile esta ocupado, se sim, verifica a cor da peça
    //se for preto, retorna a peça em minusculo
    //se não estiver ocupado retorna "-"
    @Override
    public String toString() {
        if(this.isTileOccupied()) {
            return getPieceOnTile().getPieceColor().isBlack() ?
            getPieceOnTile().toString().toLowerCase() :
            getPieceOnTile().toString();
        } else {
            return "-";
        }
    }

}
