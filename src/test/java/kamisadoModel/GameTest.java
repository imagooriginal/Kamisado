package kamisadoModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTest {

    private Board board;
    private Game game;

    @BeforeEach
    void setUp() {
        board = new Board();
        game = new Game();
    }

    @Test
    void initialGameStateIsConfiguredCorrectly() {
        assertEquals(Player.PLAYER1, game.getCurrentPlayer());
        assertNull(game.getNextMoveColor());
        assertTrue(game.isFirstMove());
        assertNull(game.getLastMovedPlayer());
        assertFalse(game.isGameOver());
        assertNull(game.getWinner());
        assertFalse(Player.PLAYER1.isWinner());
        assertFalse(Player.PLAYER2.isWinner());
    }

    @Test
    void validFirstMoveStraightAndSubsequentDiagonalMoveUpdateState() {
        // PLAYER1 moves ORANGE piece from (x=0, y=0) straight forward to (x=0, y=2) [GREEN]
        Square from1 = board.getSquare(0, 0);
        Square to1 = board.getSquare(0, 2);

        game.move(board, Player.PLAYER1, from1, to1);

        assertFalse(from1.isOccupied());
        assertTrue(to1.isOccupied());
        assertEquals(Color.ORANGE, to1.getPiece().getColor());
        assertEquals(to1, to1.getPiece().getSquare());
        assertFalse(game.isFirstMove());
        assertEquals(Player.PLAYER1, game.getLastMovedPlayer());
        assertEquals(Player.PLAYER2, game.getCurrentPlayer());
        assertEquals(Color.GREEN, game.getNextMoveColor());
        assertFalse(game.isGameOver());

        // PLAYER2 must move GREEN piece from (x=1, y=7) diagonally forward to (x=3, y=5) [PURPLE]
        Square from2 = board.getSquare(1, 7);
        Square to2 = board.getSquare(3, 5);

        game.move(board, Player.PLAYER2, from2, to2);

        assertFalse(from2.isOccupied());
        assertTrue(to2.isOccupied());
        assertEquals(Color.GREEN, to2.getPiece().getColor());
        assertEquals(Player.PLAYER2, game.getLastMovedPlayer());
        assertEquals(Player.PLAYER1, game.getCurrentPlayer());
        assertEquals(Color.PURPLE, game.getNextMoveColor());
    }

    @Test
    void moveRejectsWrongTurnEmptySourceOpponentPieceAndOccupiedDestination() {
        // Wrong player's turn
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER2, board.getSquare(0, 7), board.getSquare(0, 6)));

        // Empty source square
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER1, board.getSquare(3, 3), board.getSquare(3, 4)));

        // Moving opponent's piece
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER1, board.getSquare(0, 7), board.getSquare(0, 6)));

        // Occupied destination
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 7)));
    }

    @Test
    void moveRejectsWrongColorAfterFirstMove() {
        // PLAYER1 moves (0,0) -> (0,1) [RED], so PLAYER2 must move RED (2,7)
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 1));

        // PLAYER2 tries to move BROWN (0,7) instead of RED
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER2, board.getSquare(0, 7), board.getSquare(0, 5)));
    }

    @Test
    void moveRejectsBackwardSidewaysNonDiagonalAndBlockedPath() {
        // First move: PLAYER1 (2,0) PURPLE -> (2,2) ORANGE
        game.move(board, Player.PLAYER1, board.getSquare(2, 0), board.getSquare(2, 2));
        // Second move: PLAYER2 (7,7) ORANGE -> (7,5) GREEN
        game.move(board, Player.PLAYER2, board.getSquare(7, 7), board.getSquare(7, 5));
        // Move PLAYER1 GREEN (6,0) -> (6,3) [RED]
        game.move(board, Player.PLAYER1, board.getSquare(6, 0), board.getSquare(6, 3));
        // Move PLAYER2 RED (2,7) -> (2,6) [YELLOW]
        game.move(board, Player.PLAYER2, board.getSquare(2, 7), board.getSquare(2, 6));
        // Move PLAYER1 YELLOW (4,0) -> (4,3) [BROWN]
        game.move(board, Player.PLAYER1, board.getSquare(4, 0), board.getSquare(4, 3));

        // Non-diagonal move by PLAYER2 BROWN (0,7) -> (1,5)
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER2, board.getSquare(0, 7), board.getSquare(1, 5)));

        // Move PLAYER2 BROWN (0,7) -> (0,4) [YELLOW]
        game.move(board, Player.PLAYER2, board.getSquare(0, 7), board.getSquare(0, 4));

        // PLAYER1 YELLOW is at (4,3): backward move (4,3) -> (4,2) rejected
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER1, board.getSquare(4, 3), board.getSquare(4, 2)));

        // PLAYER1 YELLOW is at (4,3): sideways move (4,3) -> (5,3) rejected
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER1, board.getSquare(4, 3), board.getSquare(5, 3)));

        // Place a blocker directly in front of PLAYER1 YELLOW at (4,4) and verify jumping over fails
        board.getSquare(4, 4).setPiece(new Piece(Color.PINK, board.getSquare(4, 4), Player.PLAYER2));
        assertThrows(IllegalArgumentException.class,
                () -> game.move(board, Player.PLAYER1, board.getSquare(4, 3), board.getSquare(4, 5)));
    }

    @Test
    void isValidMoveDirectlyRejectsEmptySourceAndBackwardMove() {
        assertFalse(game.isValidMove(board, board.getSquare(3, 3), board.getSquare(3, 4)));

        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 2));
        assertFalse(game.isValidMove(board, board.getSquare(0, 2), board.getSquare(0, 1)));
        assertFalse(game.isValidMove(board, board.getSquare(1, 7), board.getSquare(2, 5)));
    }

    @Test
    void player1WinsByReachingRowY7() {
        // Clear (0,7) so PLAYER1's ORANGE piece at (0,0) has a clear straight path to (0,7)
        board.getSquare(0, 7).setPiece(null);

        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 7));

        assertTrue(game.isGameOver());
        assertEquals(Player.PLAYER1, game.getWinner());
        assertTrue(Player.PLAYER1.isWinner());
        assertFalse(Player.PLAYER2.isWinner());
        assertNull(game.getNextMoveColor());

        // Further moves after game over are rejected
        assertThrows(IllegalStateException.class,
                () -> game.move(board, Player.PLAYER2, board.getSquare(1, 7), board.getSquare(1, 6)));
    }

    @Test
    void player2WinsByReachingRowY0() {
        // PLAYER1 moves (0,0) ORANGE -> (0,1) [RED], making PLAYER2 move RED (2,7)
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 1));
        // Clear (2,0) so PLAYER2 RED at (2,7) can go straight to (2,0)
        board.getSquare(2, 0).setPiece(null);

        game.move(board, Player.PLAYER2, board.getSquare(2, 7), board.getSquare(2, 0));

        assertTrue(game.isGameOver());
        assertEquals(Player.PLAYER2, game.getWinner());
        assertTrue(Player.PLAYER2.isWinner());
        assertFalse(Player.PLAYER1.isWinner());
    }

    @Test
    void blockedPiecePassesTurnBackWithBlockedSquareColor() {
        // Block PLAYER2's BROWN piece at (0,7) [BROWN square]: forward squares are (0,6) and (1,6)
        movePieceDirectly(board.getSquare(1, 0), board.getSquare(0, 6));
        movePieceDirectly(board.getSquare(2, 0), board.getSquare(1, 6));

        // PLAYER1 moves (7,0) BROWN -> (5,2) [BROWN square]
        game.move(board, Player.PLAYER1, board.getSquare(7, 0), board.getSquare(5, 2));

        // PLAYER2's BROWN at (0,7) is blocked on a BROWN square -> turn passes back to PLAYER1 for BROWN
        assertFalse(game.isGameOver());
        assertEquals(Player.PLAYER1, game.getCurrentPlayer());
        assertEquals(Color.BROWN, game.getNextMoveColor());
    }

    @Test
    void deadlockCycleEndsGameAndLastMovedPlayerLoses() {
        // Block PLAYER2's BROWN piece at (0,7) [BROWN square]: forward squares (0,6) and (1,6)
        movePieceDirectly(board.getSquare(1, 0), board.getSquare(0, 6));
        movePieceDirectly(board.getSquare(2, 0), board.getSquare(1, 6));

        // Block PLAYER1's BROWN piece after it lands on (5,2) [BROWN square]: forward squares (4,3), (5,3), (6,3)
        movePieceDirectly(board.getSquare(3, 0), board.getSquare(4, 3));
        movePieceDirectly(board.getSquare(4, 0), board.getSquare(5, 3));
        movePieceDirectly(board.getSquare(5, 0), board.getSquare(6, 3));

        // PLAYER1 moves BROWN from (7,0) diagonally to (5,2) [BROWN]:
        // -> PLAYER2 BROWN at (0,7) is blocked (stands on BROWN)
        // -> passes to PLAYER1 BROWN at (5,2) which is also blocked (stands on BROWN)
        // -> loops back to PLAYER2 BROWN -> deadlock! PLAYER1 moved last, so PLAYER2 wins.
        game.move(board, Player.PLAYER1, board.getSquare(7, 0), board.getSquare(5, 2));

        assertTrue(game.isGameOver());
        assertEquals(Player.PLAYER2, game.getWinner());
        assertTrue(Player.PLAYER2.isWinner());
        assertFalse(Player.PLAYER1.isWinner());
        assertEquals(Player.PLAYER1, game.getLastMovedPlayer());
    }

    @Test
    void newGameResetsPlayerWinnerFlags() {
        board.getSquare(0, 7).setPiece(null);
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 7));
        assertTrue(Player.PLAYER1.isWinner());

        Game freshGame = new Game();
        assertFalse(freshGame.isGameOver());
        assertFalse(Player.PLAYER1.isWinner());
        assertFalse(Player.PLAYER2.isWinner());
    }

    @Test
    void getValidMovesReturnsForwardAndDiagonalUnoccupiedSquares() {
        // PLAYER1 ORANGE at (0,0): straight down col x=0 -> (0,1)..(0,6) [6 squares; (0,7) occupied]
        // diagonal down-right -> (1,1)..(6,6) [6 squares; (7,7) occupied]
        List<Square> moves = game.getValidMoves(board, board.getSquare(0, 0));
        assertEquals(12, moves.size());
        assertTrue(moves.contains(board.getSquare(0, 6)));
        assertTrue(moves.contains(board.getSquare(6, 6)));
        assertFalse(moves.contains(board.getSquare(0, 7)));

        // Empty square and opponent's piece return no moves
        assertTrue(game.getValidMoves(board, board.getSquare(3, 3)).isEmpty());
        assertTrue(game.getValidMoves(board, board.getSquare(0, 7)).isEmpty());

        // After first move, wrong color returns no moves and after game over returns no moves
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 1));
        assertTrue(game.getValidMoves(board, board.getSquare(0, 7)).isEmpty());
        assertFalse(game.getValidMoves(board, board.getSquare(2, 7)).isEmpty());

        board.getSquare(2, 0).setPiece(null);
        game.move(board, Player.PLAYER2, board.getSquare(2, 7), board.getSquare(2, 0));
        assertTrue(game.getValidMoves(board, board.getSquare(0, 1)).isEmpty());
    }

    private void movePieceDirectly(Square from, Square to) {
        Piece piece = from.getPiece();
        from.setPiece(null);
        to.setPiece(piece);
        piece.setSquare(to);
    }
}
