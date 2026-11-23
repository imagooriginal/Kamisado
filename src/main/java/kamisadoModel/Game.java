package kamisadoModel;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Player currentPlayer;
    private Color nextMoveColor;
    private boolean firstMove = true;
    private Player lastMovedPlayer;
    private boolean gameOver = false;
    private Player winner = null;
    private final boolean simulation;

    public Game() {
        currentPlayer = Player.PLAYER1;
        nextMoveColor = null; // No color restriction for the first move
        firstMove = true;
        lastMovedPlayer = null;
        gameOver = false;
        winner = null;
        simulation = false;
        Player.PLAYER1.setWinner(false);
        Player.PLAYER2.setWinner(false);
    }

    /** Creates a simulation copy of {@code other} without mutating global {@link Player} winner flags. */
    public Game(Game other) {
        this.currentPlayer = other.currentPlayer;
        this.nextMoveColor = other.nextMoveColor;
        this.firstMove = other.firstMove;
        this.lastMovedPlayer = other.lastMovedPlayer;
        this.gameOver = other.gameOver;
        this.winner = other.winner;
        this.simulation = true;
    }

    public void move(Board board, Player player, Square fromSquare, Square toSquare) {
        validateMove(board, player, fromSquare, toSquare);

        Piece movedPiece = fromSquare.getPiece();
        toSquare.setPiece(movedPiece);
        fromSquare.setPiece(null);
        movedPiece.setSquare(toSquare);

        firstMove = false;
        lastMovedPlayer = player;
        if (hasReachedGoal(player, toSquare)) {
            endGame(player);
            return;
        }
        nextMoveColor = getNextMoveColor(board, toSquare.getColor());
    }

    /**
     * Validates game state, turn order, piece ownership, color restriction,
     * direction, and clear path.
     */
    private void validateMove(Board board, Player player, Square fromSquare, Square toSquare) {
        if (gameOver) {
            throw new IllegalStateException("The game is already over!");
        }
        if (player != currentPlayer) {
            throw new IllegalArgumentException("It's not your turn!");
        }
        if (!fromSquare.isOccupied()) {
            throw new IllegalArgumentException("There is no piece to move!");
        }
        if (fromSquare.getPiece().getPlayer() != player) {
            throw new IllegalArgumentException("You can only move your own piece!");
        }
        if (toSquare.isOccupied()) {
            throw new IllegalArgumentException("The destination square is already occupied!");
        }
        if (!firstMove && fromSquare.getPiece().getColor() != nextMoveColor) {
            throw new IllegalArgumentException("You can only move the piece of the color that was last moved!");
        }
        if (!isForwardMove(player, fromSquare, toSquare)) {
            throw new IllegalArgumentException("You cannot move to this square!");
        }
        if (!isValidMove(board, fromSquare, toSquare)) {
            throw new IllegalArgumentException("Invalid move!");
        }
    }

    /** Marks the game finished and records {@code winningPlayer} as the winner. */
    private void endGame(Player winningPlayer) {
        this.gameOver = true;
        this.winner = winningPlayer;
        this.nextMoveColor = null;
        if (!simulation && winningPlayer != null) {
            winningPlayer.setWinner(true);
        }
    }

    /**
     * Checks whether {@code toSquare} advances strictly forward along the vertical
     * y-axis.
     */
    private boolean isForwardMove(Player player, Square fromSquare, Square toSquare) {
        int fromY = fromSquare.getCoordinates().getY();
        int toY = toSquare.getCoordinates().getY();
        return (player == Player.PLAYER1) ? (toY > fromY) : (toY < fromY);
    }

    /**
     * Checks whether {@code player} has reached the opponent's home row (y == 7 for
     * PLAYER1, y == 0 for PLAYER2).
     */
    private boolean hasReachedGoal(Player player, Square toSquare) {
        int targetY = toSquare.getCoordinates().getY();
        return (player == Player.PLAYER1 && targetY == 7)
                || (player == Player.PLAYER2 && targetY == 0);
    }

    public boolean isValidMove(Board board, Square fromSquare, Square toSquare) {
        if (!fromSquare.isOccupied()
                || !isForwardMove(fromSquare.getPiece().getPlayer(), fromSquare, toSquare)
                || !isStraightOrDiagonal(fromSquare, toSquare)) {
            return false;
        }
        int stepX = calculateStep(fromSquare.getCoordinates().getX(), toSquare.getCoordinates().getX());
        int stepY = calculateStep(fromSquare.getCoordinates().getY(), toSquare.getCoordinates().getY());

        return isPathClear(board.getGrid(), fromSquare, toSquare, stepX, stepY);
    }

    /**
     * Returns all legal destination squares for the piece standing on
     * {@code fromSquare}.
     */
    public List<Square> getValidMoves(Board board, Square fromSquare) {
        List<Square> validMoves = new ArrayList<>();
        if (gameOver || !fromSquare.isOccupied()) {
            return validMoves;
        }
        Piece piece = fromSquare.getPiece();
        if (piece.getPlayer() != currentPlayer || (!firstMove && piece.getColor() != nextMoveColor)) {
            return validMoves;
        }

        int stepY = (currentPlayer == Player.PLAYER1) ? 1 : -1;
        int[] stepXs = { -1, 0, 1 };
        for (int stepX : stepXs) {
            collectMovesInDirection(board.getGrid(), fromSquare, stepX, stepY, validMoves);
        }
        return validMoves;
    }

    /**
     * Walks along a single ray from {@code fromSquare} and collects unoccupied
     * destination squares.
     */
    private void collectMovesInDirection(Square[][] grid, Square fromSquare, int stepX, int stepY,
            List<Square> validMoves) {
        int x = fromSquare.getCoordinates().getX() + stepX;
        int y = fromSquare.getCoordinates().getY() + stepY;
        while (!isSquareBlocked(grid, x, y)) {
            validMoves.add(grid[y][x]);
            x += stepX;
            y += stepY;
        }
    }

    /**
     * Returns the unit step (-1, 0, or 1) along one coordinate axis from
     * {@code from} toward {@code to}.
     */
    private int calculateStep(int from, int to) {
        return Integer.compare(to, from);
    }

    /**
     * Checks whether the move covers a non-zero distance orthogonally or along a
     * 45-degree diagonal.
     */
    private boolean isStraightOrDiagonal(Square fromSquare, Square toSquare) {
        int dx = Math.abs(toSquare.getCoordinates().getX() - fromSquare.getCoordinates().getX());
        int dy = Math.abs(toSquare.getCoordinates().getY() - fromSquare.getCoordinates().getY());
        return (dx == 0 || dy == 0 || dx == dy) && (dx + dy > 0);
    }

    /**
     * Walks the path from {@code fromSquare} to {@code toSquare} and ensures every
     * stepped square is unoccupied.
     */
    private boolean isPathClear(Square[][] grid, Square fromSquare, Square toSquare, int stepX, int stepY) {
        Square moving = fromSquare;
        while (moving != toSquare) {
            int newX = moving.getCoordinates().getX() + stepX;
            int newY = moving.getCoordinates().getY() + stepY;
            if (grid[newY][newX].isOccupied()) {
                return false;
            }
            moving = grid[newY][newX];
        }
        return true;
    }

    public Color getNextMoveColor(Board board, Color color) {
        return resolveNextMoveColor(board, color, new ArrayList<>());
    }

    /**
     * Switches to the next player and resolves the color they must move, passing
     * the turn back recursively if the required piece is blocked until a cycle is
     * detected.
     */
    private Color resolveNextMoveColor(Board board, Color requiredColor, List<Piece> checkedPieces) {
        switchPlayer(currentPlayer);
        Square pieceSquare = findPieceSquare(board, currentPlayer, requiredColor);
        Piece currentPiece = pieceSquare.getPiece();

        if (checkedPieces.contains(currentPiece)) {
            if (lastMovedPlayer != null) {
                Player deadlockWinner = (lastMovedPlayer == Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
                endGame(deadlockWinner);
            } else {
                endGame(null);
            }
            return null;
        }
        checkedPieces.add(currentPiece);

        if (isPieceBlocked(board, pieceSquare, currentPlayer)) {
            // A blocked piece passes the turn back with the color of the square it stands
            // on.
            return resolveNextMoveColor(board, pieceSquare.getColor(), checkedPieces);
        }

        return requiredColor;
    }

    /**
     * Finds the square occupied by {@code player}'s piece of {@code pieceColor}.
     */
    private Square findPieceSquare(Board board, Player player, Color pieceColor) {
        Square[][] grid = board.getGrid();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (isMatchingPieceOnSquare(grid[y][x], player, pieceColor)) {
                    return grid[y][x];
                }
            }
        }
        throw new IllegalArgumentException("No piece of the specified color found for the current player.");
    }

    /**
     * Checks whether {@code square} holds {@code player}'s piece of
     * {@code pieceColor}.
     */
    private boolean isMatchingPieceOnSquare(Square square, Player player, Color pieceColor) {
        return square.isOccupied()
                && square.getPiece().getPlayer() == player
                && square.getPiece().getColor() == pieceColor;
    }

    /**
     * Checks whether all three forward squares (diagonal-left, straight,
     * diagonal-right) are blocked.
     */
    private boolean isPieceBlocked(Board board, Square pieceSquare, Player player) {
        Square[][] grid = board.getGrid();
        int x = pieceSquare.getCoordinates().getX();
        int y = pieceSquare.getCoordinates().getY();
        int forwardY = (player == Player.PLAYER1) ? y + 1 : y - 1;

        return isSquareBlocked(grid, x - 1, forwardY)
                && isSquareBlocked(grid, x, forwardY)
                && isSquareBlocked(grid, x + 1, forwardY);
    }

    /** Checks whether {@code (x, y)} is out of board bounds or already occupied. */
    private boolean isSquareBlocked(Square[][] grid, int x, int y) {
        boolean outOfBounds = (x < 0 || x >= 8 || y < 0 || y >= 8);
        return outOfBounds || grid[y][x].isOccupied();
    }

    public void switchPlayer(Player player) {
        currentPlayer = (player == Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Color getNextMoveColor() {
        return nextMoveColor;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public Player getLastMovedPlayer() {
        return lastMovedPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

}
