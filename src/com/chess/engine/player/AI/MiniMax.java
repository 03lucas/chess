package com.chess.engine.player.AI;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;

//algoritmo MiniMax compara o melhor movimento para o jogador e o pior para o oponente

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public MiniMax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(Board board) {

        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        
        System.out.println(board.currentPlayer() + " thinking with depth = " + this.searchDepth);

        int numMoves = board.currentPlayer().getLegalMoves().size();

        for(final Move move : board.currentPlayer().getLegalMoves()) {

            //faz o primeiro movimento independente do jogador
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()) {
                //se for branco, chama a funcao de menor valor para o inimigo e a maior para si
                currentValue = board.currentPlayer().getColor().isWhite() ? //pega a cor do jogador pelo tabuleiro atual
                    min(moveTransition.getToBoard(), this.searchDepth - 1) : //chama o proximo tabuleiro com a funcao de menor valor (ou seja, no proximo tabuleiro o turno e do outro jogador)
                    max(moveTransition.getToBoard(), this.searchDepth - 1);
                if(board.currentPlayer().getColor().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if(board.currentPlayer().getColor().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executionTime = System.currentTimeMillis() - startTime;

        return bestMove;
    }


    //retorna o menor valor possivel para o nivel de profundidade da arvore
    public int min(final Board board, final int depth) {

        if(depth == 0 /*|| isEndGameScenario(board)*/) {
            return this.boardEvaluator.evaluate(board, depth);//quando chega no ultimo nivel da arvore, comeca a retornar o MinMax
        }

        int lowestSeenValue = Integer.MAX_VALUE;
        //entra em cada movimento possivel
        for(final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()) {
                //chama a funcao para pegar o maior valor possivel do proximo tabuleiro apos pegar o menor valor possivel
                final int currentValue = max(moveTransition.getToBoard(), depth - 1);
                if(currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }

        return lowestSeenValue;
    }

    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckMate() ||
               board.currentPlayer().isInStaleMate();
    }

    //retorna o maior valor possivel para o nivel de profundidade da arvore
    public int max(final Board board, final int depth) {
            
            if(depth == 0 || isEndGameScenario(board)) {
                return this.boardEvaluator.evaluate(board, depth);
            }
    
            int highestSeenValue = Integer.MIN_VALUE;
            //entra em cada movimento possivel
            for(final Move move : board.currentPlayer().getLegalMoves()) {
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if(moveTransition.getMoveStatus().isDone()) {
                    final int currentValue = min(moveTransition.getToBoard(), depth - 1);
                    if(currentValue >= highestSeenValue) {
                        highestSeenValue = currentValue;
                    }
                }
            }
    
            return highestSeenValue;
    }

}
