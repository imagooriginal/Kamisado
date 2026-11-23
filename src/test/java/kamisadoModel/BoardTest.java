package kamisadoModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void boardHas8x8SquaresWithMatchingCoordinates() {
        Board board = new Board();
        Square[][] grid = board.getGrid();

        assertEquals(8, grid.length);
        for (int y = 0; y < 8; y++) {
            assertEquals(8, grid[y].length);
            for (int x = 0; x < 8; x++) {
                Square square = board.getSquare(x, y);
                assertNotNull(square);
                assertEquals(x, square.getCoordinates().getX());
                assertEquals(y, square.getCoordinates().getY());
            }
        }
    }

    @Test
    void everyRowAndColumnContainsAllEightColors() {
        Board board = new Board();

        for (int i = 0; i < 8; i++) {
            Set<Color> rowColors = EnumSet.noneOf(Color.class);
            Set<Color> colColors = EnumSet.noneOf(Color.class);
            for (int j = 0; j < 8; j++) {
                rowColors.add(board.getSquare(j, i).getColor());
                colColors.add(board.getSquare(i, j).getColor());
            }
            assertEquals(8, rowColors.size(), "Row " + i + " should contain 8 distinct colors");
            assertEquals(8, colColors.size(), "Column " + i + " should contain 8 distinct colors");
        }
    }

    @Test
    void initialPiecesArePlacedOnMatchingColorHomeSquares() {
        Board board = new Board();

        for (int x = 0; x < 8; x++) {
            Square p1Square = board.getSquare(x, 0);
            assertTrue(p1Square.isOccupied());
            assertNotNull(p1Square.getPiece());
            assertEquals(Player.PLAYER1, p1Square.getPiece().getPlayer());
            assertEquals(p1Square.getColor(), p1Square.getPiece().getColor());
            assertEquals(p1Square, p1Square.getPiece().getSquare());

            Square p2Square = board.getSquare(x, 7);
            assertTrue(p2Square.isOccupied());
            assertNotNull(p2Square.getPiece());
            assertEquals(Player.PLAYER2, p2Square.getPiece().getPlayer());
            assertEquals(p2Square.getColor(), p2Square.getPiece().getColor());
            assertEquals(p2Square, p2Square.getPiece().getSquare());
        }

        for (int y = 1; y < 7; y++) {
            for (int x = 0; x < 8; x++) {
                Square middle = board.getSquare(x, y);
                assertFalse(middle.isOccupied());
                assertNull(middle.getPiece());
            }
        }
    }
}
