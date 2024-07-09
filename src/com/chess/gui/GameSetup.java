package com.chess.gui;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.chess.engine.player.Player;
import com.chess.gui.Table.PlayerType;

import java.awt.Container;
import java.awt.GridLayout;

public class GameSetup extends JDialog {

    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;
    private JSpinner searchDepthSpinner;

    private static final String HUMAN_TEXT = "Humano";
    private static final String COMPUTER_TEXT = "Computador";

    GameSetup(final JFrame frame, final boolean modal) {
        super(frame, modal);

        final JPanel myPanel = new JPanel(new GridLayout(0, 1));

        final JRadioButton whiteHumanButton = new JRadioButton("Jogador -Branco");
        final JRadioButton blackHumanButton = new JRadioButton("Jogador - Preto");
        final JRadioButton whiteComputerButton = new JRadioButton("Computador - Branco");
        final JRadioButton blackComputerButton = new JRadioButton("Computador - Preto");
        whiteHumanButton.setActionCommand("Jogador - Branco");

        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerButton);
        whiteHumanButton.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerButton);
        blackHumanButton.setSelected(true);

        getContentPane().add(myPanel);
        myPanel.add(new JLabel("Branco"));
        myPanel.add(whiteHumanButton);
        myPanel.add(whiteComputerButton);
        myPanel.add(new JLabel("Preto"));
        myPanel.add(blackHumanButton);
        myPanel.add(blackComputerButton);

        myPanel.add(new JLabel("Analise"));
        this.searchDepthSpinner = addLabeledSpinner(myPanel, "Profundidade da analise", new SpinnerNumberModel(4, 0, 20, 1));

        final JButton cancelButton = new JButton("Cancelar");
        final JButton okButton = new JButton("OK");

        okButton.addActionListener(e -> {
            whitePlayerType = whiteHumanButton.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER;
            blackPlayerType = blackHumanButton.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER;
            GameSetup.this.setVisible(false);
        });

        cancelButton.addActionListener(e -> {
            System.out.println("Cancelar");
            GameSetup.this.setVisible(false);
        });

        myPanel.add(cancelButton);
        myPanel.add(okButton);

        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    void promptUser() {
        setVisible(true);
        repaint();
    }

    boolean isAIPlayer(final Player player) {
        if (player.getColor().isWhite()) {
            return getWhitePlayerType() == PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == PlayerType.COMPUTER;
    }

    PlayerType getWhitePlayerType() {
        return this.whitePlayerType;
    }

    PlayerType getBlackPlayerType() {
        return this.blackPlayerType;
    }

    int getSearchDepth() {
        return (int) this.searchDepthSpinner.getValue();
    }

    private static JSpinner addLabeledSpinner(final Container c,
                                              final String label,
                                              final SpinnerModel model) {
        final JLabel l = new JLabel(label);
        c.add(l);
        final JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);
        return spinner;
    }

}
