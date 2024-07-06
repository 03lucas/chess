package tests.com.chess.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.chess.engine.board.Board;
import com.chess.engine.board.Position;

public class TestBoard {

    @Test
    public void testInitialBoard() {
        final Board board = Board.createStandardBoard();
        assertEquals(board.currentPlayer().getLegalMoves().size(), 20);
        assertEquals(board.currentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertFalse(board.currentPlayer().isInCheck());
        assertFalse(board.currentPlayer().isInCheckMate());
        assertFalse(board.currentPlayer().isCastled());
        assertEquals(board.currentPlayer(), board.whitePlayer());
        assertEquals(board.currentPlayer().getOpponent(), board.blackPlayer());
        assertFalse(board.currentPlayer().getOpponent().isInCheck());
        assertFalse(board.currentPlayer().getOpponent().isInCheckMate());
        assertFalse(board.currentPlayer().getOpponent().isCastled());
        assertFalse(board.currentPlayer().getOpponent().isInStaleMate());
        assertEquals(board.whitePlayer().getPlayerKing(), board.getTileByPos(new Position(7, 4)).getPieceOnTile());
        assertEquals(board.blackPlayer().getPlayerKing(), board.getTileByPos(new Position(0, 4)).getPieceOnTile());
        assertFalse(board.currentPlayer().getOpponent().isInStaleMate());
        assertFalse(board.currentPlayer().getOpponent().isInCheck());
        assertFalse(board.currentPlayer().getOpponent().isInCheckMate());
        assertFalse(board.currentPlayer().getOpponent().isCastled());
        //assertFalse(board.currentPlayer().getOpponent().isKingSideCastleCapable());
        //assertFalse(board.currentPlayer().getOpponent().isQueenSideCastleCapable());
        assertEquals(board.currentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertEquals(board.currentPlayer().getOpponent().getOpponent().getLegalMoves().size(), 20);
    }
}
