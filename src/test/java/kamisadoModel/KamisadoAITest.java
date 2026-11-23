package kamisadoModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KamisadoAITest {

    private Board board;
    private Game game;
    private KamisadoAI ai;

    @BeforeEach
    void setUp() {
        board = new Board();
        game = new Game();
        ai = new KamisadoAI(new Random(42L));
    }

    @Test
    void easyAiAlwaysSelectsALegalMove() {
        // PLAYER1 moves (0,0) -> (0,1) [RED], so PLAYER2 must move RED (2,7)
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 1));

        Move move = ai.chooseMove(board, game, GameMode.AI_EASY);

        assertNotNull(move);
        assertEquals(board.getSquare(2, 7), move.from());
        List<Square> validMoves = game.getValidMoves(board, move.from());
        assertTrue(validMoves.contains(move.to()));

        // Executing the chosen move succeeds cleanly
        game.move(board, Player.PLAYER2, move.from(), move.to());
        assertEquals(Player.PLAYER2, game.getLastMovedPlayer());
    }

    @Test
    void hardAiTakesImmediateWinningMove() {
        // PLAYER1 moves (0,0) -> (0,1) [RED], so PLAYER2 must move RED (2,7)
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 1));
        // Move PLAYER1's PURPLE piece off (2,0) so PLAYER2 RED has an immediate winning move to (2,0)
        movePieceDirectly(board.getSquare(2, 0), board.getSquare(4, 2));

        Move move = ai.chooseMove(board, game, GameMode.AI_HARD);

        assertNotNull(move);
        assertEquals(board.getSquare(2, 7), move.from());
        assertEquals(0, move.to().getCoordinates().getY());
    }

    @Test
    void hardAiAvoidsGivingOpponentImmediateWin() {
        // PLAYER1 moves (0,0) -> (0,1) [RED], so PLAYER2 must move RED (2,7)
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 1));
        // Move PLAYER2's BROWN piece off (0,7) so PLAYER1's ORANGE piece at (0,1) would win immediately if sent to ORANGE.
        // PLAYER2's RED at (2,7) can reach (2,2) [ORANGE] (loses next turn!) or safe squares like (2,6) [YELLOW].
        movePieceDirectly(board.getSquare(0, 7), board.getSquare(6, 5));

        Move move = ai.chooseMove(board, game, GameMode.AI_HARD);

        assertNotNull(move);
        assertNotEquals(Color.ORANGE, move.to().getColor());
    }

    @Test
    void hardAiSimulationDoesNotMutateOriginalBoardGameOrPlayerWinnerFlags() {
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 1));
        movePieceDirectly(board.getSquare(2, 0), board.getSquare(4, 2));

        Move move = ai.chooseMove(board, game, GameMode.AI_HARD);

        assertNotNull(move);
        // Original board and game remain unchanged before actually applying the move
        assertTrue(board.getSquare(2, 7).isOccupied());
        assertFalse(board.getSquare(2, 0).isOccupied());
        assertEquals(Player.PLAYER2, game.getCurrentPlayer());
        assertEquals(Color.RED, game.getNextMoveColor());
        assertFalse(game.isGameOver());
        assertNull(game.getWinner());
        assertFalse(Player.PLAYER1.isWinner());
        assertFalse(Player.PLAYER2.isWinner());
    }

    @Test
    void chooseMoveReturnsNullWhenGameIsOver() {
        movePieceDirectly(board.getSquare(0, 7), board.getSquare(6, 5));
        game.move(board, Player.PLAYER1, board.getSquare(0, 0), board.getSquare(0, 7));

        assertTrue(game.isGameOver());
        assertNull(ai.chooseMove(board, game, GameMode.AI_EASY));
        assertNull(ai.chooseMove(board, game, GameMode.AI_HARD));
    }

    private void movePieceDirectly(Square from, Square to) {
        Piece piece = from.getPiece();
        from.setPiece(null);
        to.setPiece(piece);
        piece.setSquare(to);
    }
}
