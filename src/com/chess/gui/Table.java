package com.chess.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.Lists;
import com.chess.engine.player.AI.MiniMax;
import com.chess.engine.player.AI.MoveStrategy;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Table extends Observable{

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    
    private Board chessBoard;

    private Tile sourceTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highlightLegalMoves;
    private Move computerMove;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 700);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(500, 500);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(65, 65);

    private static final Table INSTANCE = new Table();

    private Table() {

        //frame do jogo
        this.gameFrame = new JFrame("Xadrez 1.0");
        //seta o layout do frame
        this.gameFrame.setLayout(new BorderLayout());
        //cria a barra de menu
        final JMenuBar tableMenuBar = createTableMenuBar(new JMenuBar());
        //associa a barra de menu do frame ao menu criado
        this.gameFrame.setJMenuBar(tableMenuBar);
        //seta o tamanho do frame
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        //cria o tabuleiro
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        //cria o painel do tabuleiro
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAiWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        //direcao do tabuleiro
        this.boardDirection = BoardDirection.NORMAL;
        //caixa que destacara os movimentos legais
        this.highlightLegalMoves = false;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        //adiciona o painel do tabuleiro ao frame
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);

        // Add a component listener to handle window resizing
        this.gameFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                boardPanel.drawBoard(chessBoard);
                gameHistoryPanel.redo(chessBoard, moveLog);
                takenPiecesPanel.redo(moveLog);
            }
        });
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(chessBoard);
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

        abstract BoardDirection opposite();
    }


    private JMenuBar createTableMenuBar(final JMenuBar tableMenuBar) {
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
        
    }

    //cria o menu de arquivo
    private JMenu createFileMenu() {

        //cria o botao na barra de menu com nome Arquivos
        final JMenu fileMenu = new JMenu("Arquivos");

        //abrir jogo por file.PGN (jogo ja jogado)
        //botao lista de opcoes
        final JMenuItem openPGN = new JMenuItem("PGN");

        //açao do botao
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Abra o arquivo PGN");
            }
        });
        fileMenu.add(openPGN);

        //cria o botao para sair do jogo
        final JMenuItem exitMenuItem = new JMenuItem("Sair");
        //acao do botao
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        //adiciona o botao ao menu do programa (Arquivos)
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferências");

        final JMenuItem flipBoardMenuItem = new JMenuItem("Virar Tabuleiro");

        flipBoardMenuItem.addActionListener(new ActionListener() {
            
            //metodo do botao para virar o tabuleiro
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });

        //adiciona o botao ao menu de preferencias
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        //caixa de selecao para destacar os movimentos
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Destacar Movimentos", false);

        //acao do botao
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });

        preferencesMenu.add(legalMoveHighlighterCheckbox);

        return preferencesMenu;
    }

    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Opções");

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");

        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());//observador
            }
        });

        optionsMenu.add(setupGameMenuItem);

        return optionsMenu;
    }

    //metodo para atualizar o jogo
    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    //observador para a IA saber quando e o turno dela
    private static class TableGameAiWatcher implements Observer {

        @Override
        public void update(final Observable o, final Object arg) {
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
               !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
               !Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                //cria um movimento
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if(Table.get().getGameBoard().currentPlayer().isInCheckMate()){
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over, " + Table.get().getGameBoard().currentPlayer().getColor().name().toLowerCase() + " player is in checkmate!",
                        "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }

            if(Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over, " + Table.get().getGameBoard().currentPlayer().getColor().name().toLowerCase() + " player is in stalemate!",
                        "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    public void updateComputerMove(final Move move) {
        this.computerMove = move;
    }

    public void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    //chama a IA
    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {

        }

        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy miniMax = new MiniMax(Table.get().getGameSetup().getSearchDepth());
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //tabuleiro
    private class BoardPanel extends JPanel {

        //lista de Tiles de tipo Position
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));

            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    final TilePanel tilePanel = new TilePanel(this, i, j);
                    // Adiciona o tilePanel a lista de tiles
                    this.boardTiles.add(tilePanel);
                    // Adiciona o tilePanel ao JPanel
                    add(tilePanel);
                }
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);

            validate();

        }

        //metodo para desenhar o tabuleiro
        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }

    }

    //painel de historico de jogadas
    public static class MoveLog {

        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }

    }

    enum PlayerType {
        HUMAN,
        COMPUTER
    }

    //casas do tabuleiro
    private class TilePanel extends JPanel {

        //posicao do tile para dar getTile na classe board
        private final Position tileId;

        TilePanel(BoardPanel boardPanel, int x, int y) {
            super(new GridBagLayout());
            this.tileId = new Position(x, y);
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {

                
                @Override
                public void mouseClicked(final MouseEvent e){
                    if(SwingUtilities.isRightMouseButton(e)){ /*cancela o movimento*/
                        sourceTile = null;
                        humanMovedPiece = null;
                        boardPanel.drawBoard(chessBoard); // Remove green dots
                    } else if(SwingUtilities.isLeftMouseButton(e)){ /*começa ou termina um movimento*/
                        //nao clicou em nenhum tile anteriormente
                        if(sourceTile == null){
                            sourceTile = chessBoard.getTileByPos(tileId);
                            humanMovedPiece = sourceTile.getPieceOnTile();
                            if(humanMovedPiece == null){
                                sourceTile = null;
                            }
                        } else { /*ja selecionou um tile*/
                            TilePanel destinationTilePanel = (TilePanel) e.getSource();
                            Tile destinationTile = chessBoard.getTileByPos(destinationTilePanel.tileId);
                            
                            //se clicou em uma peça da mesma cor, muda a peça selecionada
                            if(destinationTile.isTileOccupied() && destinationTile.getPieceOnTile().getPieceColor() == chessBoard.currentPlayer().getColor()){
                                sourceTile = destinationTile;
                                humanMovedPiece = sourceTile.getPieceOnTile();
                            } else {
                                //cria um movimento
                                final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getPosition(), destinationTile.getPosition());
                                
                                //executa o movimento
                                final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                                
                                //se o movimento for valido
                                if(transition.getMoveStatus().isDone()){
                                    //atualiza o tabuleiro
                                    chessBoard = transition.getToBoard();
                                    //adiciona o movimento ao log
                                    moveLog.addMove(move);
                                }
                                //reseta as variaveis
                                sourceTile = null;
                                humanMovedPiece = null;
                            }
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                    
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    
                }


            });

            validate();
        }

        //metodo para desenhar as casas do tabuleiro
        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        //coloca as imagens nas peças
        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTileByPos(tileId).isTileOccupied()) {
                final Piece piece = board.getTileByPos(tileId).getPieceOnTile();
                BufferedImage icon = ImageCache.getPieceImage(piece);
                final ImageIcon ic = new ImageIcon(icon);
                add(new JLabel(ic));
            }
        }

        //metodo para colorir as casas do tabuleiro
        private void assignTileColor() {
            Color lightColor = new Color(240, 217, 181); // Cor clara
            Color darkColor = new Color(181, 136, 99); // Cor escura

            if (this.tileId.getXCoord() % 2 == 0) {
                setBackground(this.tileId.getYCoord() % 2 == 0 ? lightColor : darkColor);
            } else {
                setBackground(this.tileId.getYCoord() % 2 != 0 ? lightColor : darkColor);
            }
        }
        
        //metodo para destacar os movimentos legais no tabuleiro
        private void highlightLegals(final Board board){
            //se a opcao esta ativa
            if(highlightLegalMoves){
                //verifica todos os movimentos da peca
                for (final Move move : pieceLegalMoves(board)) {
                    //se o movimento de destino for igual a casa atual adiciona um ponto verde
                    if (move.getDestinationCoordinate().equals(this.tileId)) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        }
                        catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        //metodo para pegar os movimentos legais da peça selecionada
        private Collection<Move> pieceLegalMoves(final Board board){
            //devolve os movimentos da peça selecionada se ela for da cor do jogador atual
            if(humanMovedPiece != null && humanMovedPiece.getPieceColor() == board.currentPlayer().getColor()){
                return humanMovedPiece.getPossibleMoves(board);
            }
            return Collections.emptyList();
        }

    }

}