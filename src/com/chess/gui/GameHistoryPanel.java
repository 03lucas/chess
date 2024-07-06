package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.gui.Table.MoveLog;

public class GameHistoryPanel extends JPanel{

    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);//se exceder a resolucao do tabuleiro, cria uma barra de rolagem
        this.scrollPane.setColumnHeaderView(table.getTableHeader());
        this.scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final MoveLog moveLog) {

        int currentRow = 0;
        this.model.clear();

        for (final Move move : moveLog.getMoves()) {

            final String moveText = move.toString();
            
            if (move.getMovedPiece().getPieceColor().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0); //seta o texto na celula da esquerda (0)
            } else if (move.getMovedPiece().getPieceColor().isBlack()) {
                this.model.setValueAt(moveText, currentRow, 1); //seta o texto na celula da direita (1)
                currentRow++;
            }
        }

        //calcula se no ultimo move coloca um check ou checkmate
        if(moveLog.getMoves().size() > 0) {
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();

            if(lastMove.getMovedPiece().getPieceColor().isWhite()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);//se o ultimo move foi branco, seta o texto na celula da esquerda
            } else if(lastMove.getMovedPiece().getPieceColor().isBlack()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);//se o ultimo move foi preto, seta o texto na celula da direita
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());//se exceder o limite da tabela, desce a barra de rolagem para o ultimo move
    }

    private String calculateCheckAndCheckMateHash(final Board board) {
        if(board.currentPlayer().isInCheckMate()) {
            return "#";
        } else if(board.currentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel { 

        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if(this.values == null) {
                return 0;
            }
            return this.values.size();
        }
        
        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            final Row currentRow = this.values.get(row);
            if(column == 0) {
                return currentRow.getWhiteMove();
            } else if(column == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object aValue, final int row, final int column) {
            final Row currentRow;
            if(this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if(column == 0) {
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            } else if(column == 1) {
                currentRow.setBlackMove((String) aValue);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int column) {
            return NAMES[column];
        }

    }

    private static class Row {
        String whiteMove;
        String blackMove;

        Row() {
        }

        public String getWhiteMove() {
            return this.whiteMove;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setWhiteMove(final String move) {
            this.whiteMove = move;
        }

        public void setBlackMove(final String move) {
            this.blackMove = move;
        }
        
    }

}
