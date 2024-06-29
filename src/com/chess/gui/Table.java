package com.chess.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.board.Position;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Table {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
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
        //cria o painel do tabuleiro
        this.boardPanel = new BoardPanel();
        //adiciona o painel do tabuleiro ao frame
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }


    private JMenuBar createTableMenuBar(final JMenuBar tableMenuBar) {
        tableMenuBar.add(createFileMenu());
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
                System.out.println("Open up that PGN file!");
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
            for (final TilePanel tilePanel : boardTiles) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
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

                //TODO, quando não é a vez do jogador, não deve ser possível mover as peças
                @Override
                public void mouseClicked(final MouseEvent e){
                    if(SwingUtilities.isRightMouseButton(e)){ /*cancela o movimento*/
                        sourceTile = null;
                        humanMovedPiece = null;
                    } else if(SwingUtilities.isLeftMouseButton(e)){ /*começa ou termina um movimento*/
                        //nao clicou em nenhum tile anteriormente
                        if(sourceTile == null){
                            sourceTile = chessBoard.getTileByPos(tileId);
                            humanMovedPiece = sourceTile.getPieceOnTile();
                            if(humanMovedPiece == null){
                                sourceTile = null;
                            }
                        } else { /*ja selecionou um tile*/
                            destinationTile = chessBoard.getTileByPos(tileId);

                            
                            //cria um movimento
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getPosition(), destinationTile.getPosition());
                            
                            //executa o movimento
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            
                            //se o movimento for valido
                            if(transition.getMoveStatus().isDone()){
                                //atualiza o tabuleiro
                                chessBoard = transition.getToBoard();
                                //TODO falta adicionar o movimento ao log
                            }
                            //reseta as variaveis
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
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
                            board.getTileByPos(tileId).getPieceOnTile().getPieceColor().toString().substring(0, 1) +
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
        
    }

}
