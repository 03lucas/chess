package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.gui.Table.MoveLog;

class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final long serialVersionUID = 1L;
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final Dimension TAKEN_PIECES_PANEL_DIMENSION = new Dimension(80, 80);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(Color.decode("0xFDF5E6"));
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        add(this.northPanel, BorderLayout.NORTH);
        add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_PANEL_DIMENSION);
    }

    public void redo(final MoveLog moveLog) {

        northPanel.removeAll();
        southPanel.removeAll();
    
        for (Move move : moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece killedPiece = move.getAttackedPiece();
                BufferedImage icon = null;
                try {
                    String path = "art/pieces/" + killedPiece.getPieceColor().toString().substring(0, 1) +
                            killedPiece.getPieceType().toString() + ".png";
                    icon = ImageIO.read(new File(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
    
                if (icon != null) {
                    final ImageIcon ic = new ImageIcon(icon);
                    final JLabel imageLabel = new JLabel(new ImageIcon(ic.getImage().getScaledInstance(
                            30, 30, Image.SCALE_SMOOTH)));
                    if (killedPiece.getPieceColor().isWhite()) {
                        northPanel.add(imageLabel);
                    } else {
                        southPanel.add(imageLabel);
                    }
                }
            }
        }
    
        validate();
        repaint();
    }
}