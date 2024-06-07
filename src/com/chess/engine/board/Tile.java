package com.chess.engine.board;

import java.util.HashMap;
import java.util.Map;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

//Classe abstrata faz com que não seja possivel criar "new Tile", mas sim uma vazia ou ocupada.
public abstract class Tile {

    protected final int tileCoord;

    //Criar todas as possibilidades de Tile's vazios ao carregar o código
    //para não ser necessario criar na hora, pegando do cache
    private static final Map<Integer, EmptyTile> EMPTY_TILES_Cache = createAllPossibleEmptyFiles();
    
    private static Map<Integer, EmptyTile> createAllPossibleEmptyFiles() {
        
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for(int i = 0; i< 64; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }

        //Biblioteca do google para não ser possível alterar o mapa gerado
        return ImmutableMap.copyOf(emptyTileMap);
    }
    
    //Somente é possivel criar um Tile por aqui, recebendo um tile ja existente ocupado
    //ou um vazio já existente no Map
    public static Tile createTile(final int tileCoord, final Piece piece){
        return piece != null ? new OccupiedTile(tileCoord, piece) : EMPTY_TILES_Cache.get(tileCoord);
    }

    private Tile(int tileCoord){
        this.tileCoord = tileCoord;
    }

    //Cria os métodos somente para serem sobreescrevidos
    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile{

        //final coord para não ser mudada durante o uso da API
        private EmptyTile(final int coord){
            super(coord);
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

    }

    public static final class OccupiedTile extends Tile {

        //Privada para o valor ser adquirido somente pelo getPiece
        private final Piece pieceOnTile;

        private OccupiedTile(int tileCoord, Piece pieceOnTile){
            super(tileCoord);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }
    }
    
}
