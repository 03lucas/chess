package com.chess.engine.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.chess.engine.Color;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Board {
    private final Map<Position, Tile> BOARD_MAP;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

    private Board(final Builder builder) {
        this.BOARD_MAP = createBoard(builder);
        this.whitePieces = calculateActivePieces(this.BOARD_MAP, Color.WHITE);
        this.blackPieces = calculateActivePieces(this.BOARD_MAP, Color.BLACK);

        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calcLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calcLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final String tileText = this.getTile(i, j).toString();
                builder.append(String.format("%3s", tileText));
                if (j == 7) {
                    builder.append("\n");
                }
            }
        }

        return builder.toString();
    }

    //calcular os movimentos legais de cada jogador
    private Collection<Move> calcLegalMoves(final Collection<Piece> pieces) {
        
        final List<Move> legalMoves = new ArrayList<>();

        for (Piece piece : pieces) {
            legalMoves.addAll(piece.getPossibleMoves(this));
        }
        
        return ImmutableList.copyOf(legalMoves);
    }

    //pegar as peças ativas
    private static Collection<Piece> calculateActivePieces(Map<Position, Tile> board_MAP, Color color) {

        final List<Piece> pieces = new ArrayList<>();

        for (Tile tile : board_MAP.values()) {

            if (tile.isTileOccupied()) {
                Piece piece = tile.getPieceOnTile();
                if (piece.getPieceColor() == color) {
                    pieces.add(piece);
                }
            }

        }

        return ImmutableList.copyOf(pieces);
    }

    //informa as coordenadas para o construtor privado retornar o Tile
    public Tile getTile(final int xCoord, final int yCoord) {
        return this.getTile(new Position(xCoord, yCoord));
    }

    //retorna o tile por Position
    public Tile getTileByPos(final Position position) {
        return this.getTile(position);
    }

    //pega os Tiles do Map
    private Tile getTile(final Position position) {
        return BOARD_MAP.get(position);
    }

    private Map<Position, Tile> createBoard(Builder builder) {

        final Map<Position, Tile> boardMap = new HashMap<>();

        //cria as tile's
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position position = new Position(i, j);

                //se a posicao exite no builder
                if(builder.boardConfig.containsKey(position)){
                    //cria um tile com a peça associada a posição
                    Tile tile = Tile.getInstance(position);
                    //coloca a peça no tile
                    tile.setPieceOnTile(builder.boardConfig.get(position));
                    //coloca o tile no mapa
                    boardMap.put(position, tile);
                } else {
                    //cria um tile sem peça
                    Tile tile = Tile.getInstance(position);
                    boardMap.put(position, tile);
                }
            }
        }

        return ImmutableMap.copyOf(boardMap);
    }

    public static Board createStandardBoard() {
        final Builder builder = new Builder();

        // Black Layout
        builder.setPiece(new Rook(new Position(0, 0), Color.BLACK, true));
        builder.setPiece(new Knight(new Position(0, 1), Color.BLACK, true));
        builder.setPiece(new Bishop(new Position(0, 2), Color.BLACK, true));
        builder.setPiece(new Queen(new Position(0, 3), Color.BLACK, true));
        builder.setPiece(new King(new Position(0, 4), Color.BLACK, true));
        builder.setPiece(new Bishop(new Position(0, 5), Color.BLACK, true));
        builder.setPiece(new Knight(new Position(0, 6), Color.BLACK, true));
        builder.setPiece(new Rook(new Position(0, 7), Color.BLACK, true));
        builder.setPiece(new Pawn(new Position(1, 0), Color.BLACK, true));
        builder.setPiece(new Pawn(new Position(1, 1), Color.BLACK, true));
        builder.setPiece(new Pawn(new Position(1, 2), Color.BLACK, true));
        builder.setPiece(new Pawn(new Position(1, 3), Color.BLACK, true));
        builder.setPiece(new Pawn(new Position(1, 4), Color.BLACK, true));
        builder.setPiece(new Pawn(new Position(1, 5), Color.BLACK, true));
        builder.setPiece(new Pawn(new Position(1, 6), Color.BLACK, true));
        builder.setPiece(new Pawn(new Position(1, 7), Color.BLACK, true));

        // White Layout
        builder.setPiece(new Rook(new Position(7, 0), Color.WHITE, true));
        builder.setPiece(new Knight(new Position(7, 1), Color.WHITE, true));
        builder.setPiece(new Bishop(new Position(7, 2), Color.WHITE, true));
        builder.setPiece(new Queen(new Position(7, 3), Color.WHITE, true));
        builder.setPiece(new King(new Position(7, 4), Color.WHITE, true));
        builder.setPiece(new Bishop(new Position(7, 5), Color.WHITE, true));
        builder.setPiece(new Knight(new Position(7, 6), Color.WHITE, true));
        builder.setPiece(new Rook(new Position(7, 7), Color.WHITE, true));
        builder.setPiece(new Pawn(new Position(6, 0), Color.WHITE, true));
        builder.setPiece(new Pawn(new Position(6, 1), Color.WHITE, true));
        builder.setPiece(new Pawn(new Position(6, 2), Color.WHITE, true));
        builder.setPiece(new Pawn(new Position(6, 3), Color.WHITE, true));
        builder.setPiece(new Pawn(new Position(6, 4), Color.WHITE, true));
        builder.setPiece(new Pawn(new Position(6, 5), Color.WHITE, true));
        builder.setPiece(new Pawn(new Position(6, 6), Color.WHITE, true));
        builder.setPiece(new Pawn(new Position(6, 7), Color.WHITE, true));

        //define quem começa o jogo
        builder.setMoveMaker(Color.WHITE);

        return builder.build();
    }

    //retorna os movimentos legais de ambos jogadores
    public Collection<Move> getAllLegalMoves() {
        return Stream.concat(this.whitePlayer.getLegalMoves().stream(), this.blackPlayer.getLegalMoves().stream()).collect(Collectors.toList());
    }

    //builder principal
    public static class Builder {
        Map<Position, Piece> boardConfig;
        Color nextMoveMaker;
        Pawn enPassantPawn;

        public Builder() {
            this.boardConfig = new HashMap<Position, Piece>();
        }

        //associa uma peça a uma posição
        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        //define quem faz a proxima jogada
        public Builder setMoveMaker(final Color nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build() {
            return new Board(this);
        }

        public Builder setEnPassantPawn(final Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
            return this;
        }
    }

    public Player whitePlayer() {
        return this.whitePlayer;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    //se tem peao enPassant
    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }
}