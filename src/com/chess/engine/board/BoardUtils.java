package com.chess.engine.board;


public class BoardUtils {

    //caso tentem criar uma instancia de BoardUtils, lança uma exceção
    private BoardUtils(){
        throw new RuntimeException("Ação Inválida");//não é possível instanciar BoardUtils
    }

    //verifica se a posição não é out of bounds
    public static boolean isValidTileCoord(final Position position){
        return position.getXCoord() >=0 &&  position.getXCoord() < 8 && 
        position.getYCoord() >= 0 && position.getYCoord() < 8;
    }

}
