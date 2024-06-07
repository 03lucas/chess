package com.chess.engine.board;

public class BoardUtils {

    private BoardUtils(){
        throw new RuntimeException("Invalid Action");
    }

    public static boolean isValidTileCoord(int coord){
        return coord >=0 && coord < 64;
    }

}
