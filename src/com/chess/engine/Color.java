package com.chess.engine;

import com.chess.engine.board.Position;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

public enum Color {

    WHITE {

        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public boolean isPawnPromotionSquare(Position position) {
            return position.getXCoord() == 0;
        }
    },
    BLACK {

        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public boolean isPawnPromotionSquare(Position position) {
            return position.getXCoord() == 7;
        }
    };

    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract int getDirection();
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
    public abstract boolean isPawnPromotionSquare(Position position);
}
