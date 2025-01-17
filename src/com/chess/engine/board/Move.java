package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {
    
    protected final Board board; //incoming board
    protected final Piece movedPiece;
    protected final Position destinationCoordinate;

    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece movedPiece, final Position destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (this.destinationCoordinate != null ? this.destinationCoordinate.hashCode() : 0);
        result = 31 * result + (this.movedPiece != null ? this.movedPiece.hashCode() : 0);

        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return (getDestinationCoordinate() != null ? getDestinationCoordinate().equals(otherMove.getDestinationCoordinate()) : otherMove.getDestinationCoordinate() == null) &&
            (getMovedPiece() != null ? getMovedPiece().equals(otherMove.getMovedPiece()) : otherMove.getMovedPiece() == null) &&
            (getCurrentCoordinate() != null ? getCurrentCoordinate().equals(otherMove.getCurrentCoordinate()) : otherMove.getCurrentCoordinate() == null);
    }

    //retorna a coordenada de destino
    public Position getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    //retorna a peça movida
    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    //retorna a coordenada de origem
    public Position getCurrentCoordinate() {
        return this.movedPiece.getPiecePosition();
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    //não modifica o tabuleiro existente, cria um novo tabuleiro para o movimento
    public Board execute() {

        final Board.Builder builder = new Board.Builder();

        //coloca todas as peças ativas do jogador atual no novo tabuleiro
        for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
            //se a peça não for a peça movida, coloca a peça no novo tabuleiro
            if (!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }

        //coloca todas as peças ativas do oponente no novo tabuleiro
        for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        //move a peça para a nova posição
        builder.setPiece(this.movedPiece.movePiece(this));
        //troca o jogador
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            
        return builder.build();
    }

    public static final class MajorMove extends Move {

        public MajorMove(final Board board, final Piece movedPiece, final Position destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        //primeira letra da peca + coordenada de destino
        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class MajorAttackMove extends AttackMove {

        public MajorAttackMove(final Board board, final Piece movedPiece, final Position destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + "x" + BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class AttackMove extends Move {
        final Piece attackedPiece;

        AttackMove(final Board board, final Piece movedPiece, final Position destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }

    }

    public static final class PawnMove extends Move {

        public PawnMove(final Board board, final Piece movedPiece, final Position destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class PawnPromotion extends Move {

        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.board, decoratedMove.movedPiece, decoratedMove.destinationCoordinate);
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public Board execute() {

            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Board.Builder();

            //coloca todas as peças ativas do jogador atual no novo tabuleiro
            for (final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()) {
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            //coloca todas as peças ativas do oponente no novo tabuleiro
            for (final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            //move e transforma o peao na peca promovida desejada
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            //troca o turno
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getOpponent().getColor());

            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public int hashCode() {
            return this.decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && super.equals(other);
        }

        @Override
        public String toString() {
            return "";
        }

    }

    public static class  PawnAttackMove extends AttackMove {

        public PawnAttackMove(final Board board, final Piece movedPiece, final Position destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) + "x" +
                BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final Position destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute() {

            final Board.Builder builder = new Board.Builder();

            //coloca todas as peças ativas do jogador atual no novo tabuleiro
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            //coloca todas as peças ativas do oponente no novo tabuleiro
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                if (!piece.equals(this.getAttackedPiece())) {
                    builder.setPiece(piece);
                }
            }

            //move a peça para a nova posição
            builder.setPiece(this.movedPiece.movePiece(this));
            //troca o turno
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());

            return builder.build();
        }

    }

    public static final class PawnJump extends Move {

        public PawnJump(final Board board, final Piece movedPiece, final Position destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {

            final Board.Builder builder = new Board.Builder();

            //coloca todas as peças ativas do jogador atual no novo tabuleiro
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            //coloca todas as peças ativas do oponente no novo tabuleiro
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            //move a peça para a nova posição
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);//ao pular, marca o peão como passivel a receber en passant
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());

            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    static abstract class CastleMove extends Move {

        final Rook castleRook;
        final Position castleRookDestination;

        public CastleMove(final Board board, final Piece movedPiece, final Position destinationCoordinate,
        final Rook castleRook, final Position castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {

            final Board.Builder builder = new Board.Builder();

            //coloca todas as peças ativas do jogador atual no novo tabuleiro
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            //coloca todas as peças ativas do oponente no novo tabuleiro
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            //move a peça para a nova posição
            builder.setPiece(this.movedPiece.movePiece(this));
            //move a torre para a nova posição
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceColor(), false));
            //troca o turno
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());

            return builder.build();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }

    }

    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(final Board board, final Piece movedPiece, final Position destinationCoordinate,
        final Rook castleRook, final Position castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() {
            return "O-O";
        }

    }

    public static final class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(final Board board, final Piece movedPiece, final Position destinationCoordinate,
        final Rook castleRook, final Position castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() {
            return "O-O-O";
        }

    }

    public static final class NullMove extends Move {

        public NullMove() {
            super(null, null, new Position(-1,-1));
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Não é possível executar um NullMove");
        }

    }
    
    public static class MoveFactory {

        private MoveFactory() {
            throw new RuntimeException("Não é possível instanciar MoveFactory");
        }

        public static Move createMove(final Board board, final Position currentCoordinate, final Position destinationCoordinate) {
            
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate().equals(currentCoordinate) &&
                        move.getDestinationCoordinate().equals(destinationCoordinate)) {
                    return move;
                }
            }

            return NULL_MOVE;
        }

    }






    //
    // ENUM MoveStatus
    //
    public enum MoveStatus {

        DONE {
            @Override
            public boolean isDone() {
                return true;
            }
        },
        ILLEGAL_MOVE {
            @Override
            public boolean isDone() {
                return false;
            }
        },
        LEAVES_PLAYER_IN_CHECK {
            @Override
            public boolean isDone() {
                return false;
            }
        };

        public abstract boolean isDone();

    }
    
}
