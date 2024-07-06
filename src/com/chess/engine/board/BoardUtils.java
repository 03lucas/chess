package com.chess.engine.board;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum BoardUtils {

    INSTANCE;

    //position(7,0) = a1 etc
    public final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    public final Map<String, Position> positionToCoordinate = initializePositionToCoordinateMap();

    //verifica se a posição não é out of bounds
    public static boolean isValidTileCoord(final Position position){
        return position.getXCoord() >=0 &&  position.getXCoord() < 8 && 
        position.getYCoord() >= 0 && position.getYCoord() < 8;
    }

    public Position getCoordinateAtPosition(final String position){
        return positionToCoordinate.get(position);
        
    }

    private Map<String, Position> initializePositionToCoordinateMap() {
        final Map<String, Position> positionToCoordinate = new HashMap<>();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                //i=4, j=4 -> 4 * 8 + 4 = 36, index 36 na lista ou seja, e4
                positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i*8 + j), new Position(i,j));
            }
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }

    public String getPositionAtCoordinate(final Position coordinate){
        //converte as coordenadas de Position para um index
        int index = coordinate.getXCoord() * 8 + coordinate.getYCoord();
        return ALGEBRAIC_NOTATION.get(index);
    }

    private static List<String> initializeAlgebraicNotation() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
    }

}
