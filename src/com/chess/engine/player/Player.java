package com.chess.engine.player;

import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.PieceType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Color;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveStatus;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.board.Position;

public abstract class Player {
    
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {

        this.board = board;
        this.playerKing = establishKing();
        
        //concatena os movimentos legais com os movimentos de castle
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));

        //não pode ser um boolean de true ou false pq
        //construtor de board -> construtor de player -> isInCheck -> 
        //hasEscapeMoves -> makeMove -> fazer um move constroi um board
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    //verifica se o rei está em cheque passando por todos os ataques possiveis inimigos
    static Collection<Move> calculateAttacksOnTile(Position piecePosition, Collection<Move> moves) {

        final List<Move> attackMoves = new ArrayList<>();

        for (final Move move : moves) {

            if (piecePosition.equals(move.getDestinationCoordinate())) {
                attackMoves.add(move);
            }

        }

        return ImmutableList.copyOf(attackMoves);
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    //verifica se o rei ainda está no tabuleiro
    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType() == PieceType.KING) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Não deve chegar aqui. Tabuleiro inválido");
    }

    //rei em cheque
    public boolean isInCheck() {
        return this.isInCheck;
    }

    //cheque-mate
    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    //nao esta em cheque e nao tem movimentos possiveis
    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    //verifica se o rei tem movimentos possiveis
    protected boolean hasEscapeMoves() {
        for (final Move move : this.legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCastled() {
        return false;
    }

    //faz o movimento
    public MoveTransition makeMove(final Move move) {

        //verifica se o movimento é legal, se não for retorna um movimento ilegal
        //e o mesmo tabuleiro
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        //cria um novo tabuleiro para o movimento
        final Board transitionBoard = move.execute();

        //calcula os ataques no rei
        //não é possivel fazer um movimento que coloque o rei em cheque
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                                                                            transitionBoard.currentPlayer().getLegalMoves());

        //tem ataques no rei
        if(!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    //rei do jogador
    public King getPlayerKing() {
        return this.playerKing;
    }

    //verifica se o movimento é legal
    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    protected boolean hasCastleOpportunities() {
        return false;
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Color getColor();
    public abstract Player getOpponent();
    public abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals);

}