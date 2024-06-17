package com.chess.engine.board;

import com.chess.engine.board.Move.MoveStatus;

public class MoveTransition {

    private final Board transitionBoard;
    private final Move transitionMove;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board transitionBoard, final Move move, final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.transitionMove = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }


    public Board getToBoard() {
         return this.transitionBoard;
    }

    public Move getTransitionMove() {
        return this.transitionMove;
    }

}
