package kamisadoModel;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Player currentPlayer;
    private Color nextMoveColor;
    private Boolean firstMove = true;
    private Player lastMovedPlayer;

    public Game() {
        // Board board = new Board();
        currentPlayer = Player.PLAYER1;
        nextMoveColor = null; // No color restriction for the first move
        lastMovedPlayer = null;
        Player.PLAYER1.setWinner(false);
        Player.PLAYER2.setWinner(false);

    }

    public void move(Board board, Player player, Square fromSquare, Square toSquare) {
        if (player != currentPlayer) {
            throw new IllegalArgumentException("It's not your turn!");
        }
        if (!fromSquare.isOccupied()) {
            throw new IllegalArgumentException("There is no piece to move!");
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

        fromSquare.setOccupied(false);
        toSquare.setOccupied(true);
        toSquare.setPiece(fromSquare.getPiece());
        fromSquare.setPiece(null);
        toSquare.getPiece().setSquare(toSquare);

        firstMove = false;
        lastMovedPlayer = player;
        if (hasReachedGoal(player, toSquare)) {
            player.setWinner(true);
            throw new IllegalArgumentException("VÉGE A JÁTÉKNAK!");
        }
        nextMoveColor = getNextMoveColor(board, toSquare.getColor());

    }

    /** Checks whether {@code toSquare} advances strictly forward in {@code player}'s direction. */
    private boolean isForwardMove(Player player, Square fromSquare, Square toSquare) {
        int fromY = fromSquare.getCoordinates().getY();
        int toY = toSquare.getCoordinates().getY();
        return (player == Player.PLAYER1) ? (toY > fromY) : (toY < fromY);
    }

    /** Checks whether {@code player} has reached the opponent's home row. */
    private boolean hasReachedGoal(Player player, Square toSquare) {
        int targetY = toSquare.getCoordinates().getY();
        return (player == Player.PLAYER1 && targetY == 7)
                || (player == Player.PLAYER2 && targetY == 0);
    }

    public boolean isValidMove(Board board, Square fromSquare, Square toSquare) {
        if (!isStraightOrDiagonal(fromSquare, toSquare)) {
            return false;
        }
        int stepX = calculateStep(fromSquare.getCoordinates().getX(), toSquare.getCoordinates().getX());
        int stepY = calculateStep(fromSquare.getCoordinates().getY(), toSquare.getCoordinates().getY());

        return isPathClear(board.getGrid(), fromSquare, toSquare, stepX, stepY);
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
            if (grid[newX][newY].isOccupied()) {
                return false;
            }
            moving = grid[newX][newY];
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
                Player winner = (lastMovedPlayer == Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
                winner.setWinner(true);
            }
            throw new IllegalArgumentException("VÉGE A JÁTÉKNAK!");
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isMatchingPieceOnSquare(grid[i][j], player, pieceColor)) {
                    return grid[i][j];
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

    /** Checks whether all three forward squares are blocked. */
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
        return outOfBounds || grid[x][y].isOccupied();
    }

    public void switchPlayer(Player player) {
        currentPlayer = (player == Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
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

}
