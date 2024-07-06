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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Table {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;
    
    private Board chessBoard;

    private Tile sourceTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highlightLegalMoves;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 700);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(500, 500);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(65, 65);
    private static final String PieceIconPath = "art/pieces/";


    public Table() {

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
        //direcao do tabuleiro
        this.boardDirection = BoardDirection.NORMAL;
        //caixa que destacara os movimentos legais
        this.highlightLegalMoves = true;
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
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", true);

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
                                boardPanel.drawBoard(chessBoard);
                            }
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
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
                try {
                    final BufferedImage image = ImageIO.read(new File(PieceIconPath + /*diretorio das imagens*/
                            //pega a primeira letra da cor da peça e a peça em si
                            //ex: BKing.gif, B = Black, King = Rei
                            board.getTileByPos(tileId).getPieceOnTile().getPieceColor().toString().charAt(0) +
                            board.getTileByPos(tileId).getPieceOnTile().toString() +
                            ".png"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                //pega os movimentos legais da peça selecionada
                Collection<Move> legalMoves = pieceLegalMoves(board);
                //verifica se o tile atual está nos movimentos legais
                if(legalMoves.stream().anyMatch(move -> move.getDestinationCoordinate().equals(this.tileId))){
                    try{/*desenha um botao verde na casa*/
                        add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                    } catch(Exception e){
                        e.printStackTrace();
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