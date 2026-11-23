package kamisadoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** AI opponent supporting Easy (random legal move) and Hard (alpha-beta Minimax) difficulties. */
public class KamisadoAI {

    private static final int HARD_SEARCH_DEPTH = 3;
    private static final int WIN_SCORE = 10_000;

    private final Random random;

    public KamisadoAI() {
        this(new Random());
    }

    public KamisadoAI(Random random) {
        this.random = random;
    }

    /** Selects a legal move for the current player according to {@code mode}, or {@code null} if none exists. */
    public Move chooseMove(Board board, Game game, GameMode mode) {
        if (game.isGameOver()) {
            return null;
        }
        List<Move> legalMoves = getAllLegalMoves(board, game);
        if (legalMoves.isEmpty()) {
            return null;
        }
        if (mode == GameMode.AI_HARD) {
            return chooseBestMove(board, game, legalMoves);
        }
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }

    private Move chooseBestMove(Board board, Game game, List<Move> legalMoves) {
        Player aiPlayer = game.getCurrentPlayer();
        int bestScore = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<>();

        for (Move move : legalMoves) {
            int score = evaluateCandidateMove(board, game, move, aiPlayer);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private int evaluateCandidateMove(Board board, Game game, Move move, Player aiPlayer) {
        Board boardCopy = new Board(board);
        Game gameCopy = new Game(game);
        applyMoveOnCopy(boardCopy, gameCopy, move);
        return minimax(boardCopy, gameCopy, HARD_SEARCH_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, aiPlayer);
    }

    private int minimax(Board board, Game game, int depth, int alpha, int beta, Player aiPlayer) {
        if (game.isGameOver()) {
            return evaluateTerminal(game, depth, aiPlayer);
        }
        if (depth == 0) {
            return evaluatePosition(board, game, aiPlayer);
        }

        List<Move> legalMoves = getAllLegalMoves(board, game);
        if (legalMoves.isEmpty()) {
            return evaluatePosition(board, game, aiPlayer);
        }

        if (game.getCurrentPlayer() == aiPlayer) {
            return maximize(board, game, legalMoves, depth, alpha, beta, aiPlayer);
        }
        return minimize(board, game, legalMoves, depth, alpha, beta, aiPlayer);
    }

    private int maximize(Board board, Game game, List<Move> legalMoves, int depth, int alpha, int beta,
            Player aiPlayer) {
        int best = Integer.MIN_VALUE;
        int currentAlpha = alpha;
        for (Move move : legalMoves) {
            Board boardCopy = new Board(board);
            Game gameCopy = new Game(game);
            applyMoveOnCopy(boardCopy, gameCopy, move);

            int score = minimax(boardCopy, gameCopy, depth - 1, currentAlpha, beta, aiPlayer);
            best = Math.max(best, score);
            currentAlpha = Math.max(currentAlpha, best);
            if (beta <= currentAlpha) {
                break;
            }
        }
        return best;
    }

    private int minimize(Board board, Game game, List<Move> legalMoves, int depth, int alpha, int beta,
            Player aiPlayer) {
        int best = Integer.MAX_VALUE;
        int currentBeta = beta;
        for (Move move : legalMoves) {
            Board boardCopy = new Board(board);
            Game gameCopy = new Game(game);
            applyMoveOnCopy(boardCopy, gameCopy, move);

            int score = minimax(boardCopy, gameCopy, depth - 1, alpha, currentBeta, aiPlayer);
            best = Math.min(best, score);
            currentBeta = Math.min(currentBeta, best);
            if (currentBeta <= alpha) {
                break;
            }
        }
        return best;
    }

    private void applyMoveOnCopy(Board boardCopy, Game gameCopy, Move move) {
        Coordinates fromCoord = move.from().getCoordinates();
        Coordinates toCoord = move.to().getCoordinates();
        Square copyFrom = boardCopy.getSquare(fromCoord.getX(), fromCoord.getY());
        Square copyTo = boardCopy.getSquare(toCoord.getX(), toCoord.getY());
        gameCopy.move(boardCopy, gameCopy.getCurrentPlayer(), copyFrom, copyTo);
    }

    private int evaluateTerminal(Game game, int depth, Player aiPlayer) {
        if (game.getWinner() == aiPlayer) {
            return WIN_SCORE + depth;
        }
        return -WIN_SCORE - depth;
    }

    private int evaluatePosition(Board board, Game game, Player aiPlayer) {
        int score = calculateAdvancementScore(board, aiPlayer);
        List<Move> activeMoves = getAllLegalMoves(board, game);
        boolean aiTurn = (game.getCurrentPlayer() == aiPlayer);

        int mobilityBonus = activeMoves.size() * 2 + 15;
        score += aiTurn ? mobilityBonus : -mobilityBonus;

        if (hasImmediateWinningMove(activeMoves, game.getCurrentPlayer())) {
            score += aiTurn ? 800 : -800;
        }
        return score;
    }

    private int calculateAdvancementScore(Board board, Player aiPlayer) {
        int score = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Square square = board.getSquare(x, y);
                if (square.isOccupied()) {
                    Player owner = square.getPiece().getPlayer();
                    int progress = (owner == Player.PLAYER1) ? y : (7 - y);
                    score += (owner == aiPlayer) ? (progress * 5) : -(progress * 5);
                }
            }
        }
        return score;
    }

    private boolean hasImmediateWinningMove(List<Move> moves, Player player) {
        int goalY = (player == Player.PLAYER1) ? 7 : 0;
        for (Move move : moves) {
            if (move.to().getCoordinates().getY() == goalY) {
                return true;
            }
        }
        return false;
    }

    /** Collects all legal moves available for the active player in the current game state. */
    public List<Move> getAllLegalMoves(Board board, Game game) {
        List<Move> moves = new ArrayList<>();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Square fromSquare = board.getSquare(x, y);
                for (Square toSquare : game.getValidMoves(board, fromSquare)) {
                    moves.add(new Move(fromSquare, toSquare));
                }
            }
        }
        return moves;
    }
}
