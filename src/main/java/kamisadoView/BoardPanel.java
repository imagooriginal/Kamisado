package kamisadoView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import kamisadoModel.Board;
import kamisadoModel.Game;
import kamisadoModel.Piece;
import kamisadoModel.Player;
import kamisadoModel.Square;

/** Renders the 8x8 Kamisado board, pieces, and legal moves, and handles player clicks. */
public class BoardPanel extends JPanel {

    private static final int BOARD_SIZE = 8;
    private static final int DEFAULT_TILE_SIZE = 72;

    private transient Board board;
    private transient Game game;
    private transient Square selectedSquare;
    private transient List<Square> validMoves = new ArrayList<>();
    private final transient Runnable onStateChanged;
    private boolean inputEnabled = true;

    public BoardPanel(Board board, Game game, Runnable onStateChanged) {
        this.board = board;
        this.game = game;
        this.onStateChanged = onStateChanged;
        setPreferredSize(new Dimension(BOARD_SIZE * DEFAULT_TILE_SIZE, BOARD_SIZE * DEFAULT_TILE_SIZE));
        syncRequiredSelection();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    /** Resets the panel to a new game and board instance. */
    public void reset(Board newBoard, Game newGame) {
        this.board = newBoard;
        this.game = newGame;
        this.inputEnabled = true;
        refresh();
    }

    /** Synchronizes mandatory selection and repaints the board after an external (e.g. AI) move. */
    public void refresh() {
        syncRequiredSelection();
        repaint();
    }

    /** Enables or disables player mouse clicks (disabled while the AI is taking its turn). */
    public void setInputEnabled(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }

    private int calculateTileSize() {
        int available = Math.min(getWidth(), getHeight());
        return Math.max(1, available / BOARD_SIZE);
    }

    private void handleMouseClick(int mouseX, int mouseY) {
        if (!inputEnabled || game.isGameOver()) {
            return;
        }
        int tileSize = calculateTileSize();
        int boardPixels = tileSize * BOARD_SIZE;
        int offsetX = (getWidth() - boardPixels) / 2;
        int offsetY = (getHeight() - boardPixels) / 2;

        int relX = mouseX - offsetX;
        int relY = mouseY - offsetY;
        if (relX < 0 || relX >= boardPixels || relY < 0 || relY >= boardPixels) {
            return;
        }

        int x = relX / tileSize;
        int y = (BOARD_SIZE - 1) - (relY / tileSize);
        Square clickedSquare = board.getSquare(x, y);
        if (selectedSquare != null && validMoves.contains(clickedSquare)) {
            game.move(board, game.getCurrentPlayer(), selectedSquare, clickedSquare);
            syncRequiredSelection();
            repaint();
            onStateChanged.run();
            return;
        }

        trySelectPiece(clickedSquare);
    }

    private void trySelectPiece(Square square) {
        if (!square.isOccupied()) {
            if (game.isFirstMove()) {
                clearSelection();
                repaint();
            }
            return;
        }

        Piece piece = square.getPiece();
        boolean isCurrentPlayersPiece = piece.getPlayer() == game.getCurrentPlayer();
        boolean matchesRequiredColor = game.isFirstMove() || piece.getColor() == game.getNextMoveColor();
        if (isCurrentPlayersPiece && matchesRequiredColor) {
            selectedSquare = square;
            validMoves = game.getValidMoves(board, square);
            repaint();
        }
    }

    /**
     * Automatically selects the mandatory piece after the first move so the active player
     * can only move that single required piece.
     */
    private void syncRequiredSelection() {
        if (game.isGameOver() || game.isFirstMove()) {
            clearSelection();
            return;
        }
        Square requiredSquare = findRequiredPieceSquare();
        if (requiredSquare != null) {
            selectedSquare = requiredSquare;
            validMoves = game.getValidMoves(board, requiredSquare);
        } else {
            clearSelection();
        }
    }

    private Square findRequiredPieceSquare() {
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                Square square = board.getSquare(x, y);
                if (square.isOccupied()
                        && square.getPiece().getPlayer() == game.getCurrentPlayer()
                        && square.getPiece().getColor() == game.getNextMoveColor()) {
                    return square;
                }
            }
        }
        return null;
    }

    private void clearSelection() {
        selectedSquare = null;
        validMoves = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int tileSize = calculateTileSize();
        int boardPixels = tileSize * BOARD_SIZE;
        int offsetX = (getWidth() - boardPixels) / 2;
        int offsetY = (getHeight() - boardPixels) / 2;

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                Square square = board.getSquare(x, y);
                int drawX = offsetX + x * tileSize;
                int drawY = offsetY + (BOARD_SIZE - 1 - y) * tileSize;

                drawSquare(g2d, square, drawX, drawY, tileSize);
                if (square == selectedSquare) {
                    drawSelectionBorder(g2d, drawX, drawY, tileSize);
                }
                if (validMoves.contains(square)) {
                    drawValidMoveMarker(g2d, drawX, drawY, tileSize);
                }
                if (square.isOccupied()) {
                    drawPiece(g2d, square.getPiece(), drawX, drawY, tileSize);
                }
            }
        }

        g2d.dispose();
    }

    private void drawSquare(Graphics2D g2d, Square square, int drawX, int drawY, int tileSize) {
        g2d.setColor(ColorMapper.toAwtColor(square.getColor()));
        g2d.fillRect(drawX, drawY, tileSize, tileSize);
        g2d.setColor(new Color(40, 40, 40));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRect(drawX, drawY, tileSize, tileSize);
    }

    private void drawSelectionBorder(Graphics2D g2d, int drawX, int drawY, int tileSize) {
        float strokeWidth = Math.max(2f, tileSize / 18f);
        int inset = Math.max(2, Math.round(strokeWidth / 2f) + 1);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.drawRect(drawX + inset, drawY + inset, tileSize - 2 * inset, tileSize - 2 * inset);
    }

    private void drawValidMoveMarker(Graphics2D g2d, int drawX, int drawY, int tileSize) {
        int diameter = Math.max(8, tileSize / 3);
        int offset = (tileSize - diameter) / 2;
        g2d.setColor(new Color(255, 255, 255, 190));
        g2d.fillOval(drawX + offset, drawY + offset, diameter, diameter);
        g2d.setColor(new Color(20, 20, 20, 180));
        g2d.setStroke(new BasicStroke(Math.max(1.5f, tileSize / 36f)));
        g2d.drawOval(drawX + offset, drawY + offset, diameter, diameter);
    }

    private void drawPiece(Graphics2D g2d, Piece piece, int drawX, int drawY, int tileSize) {
        int outerPadding = Math.max(4, tileSize / 7);
        int outerDiameter = tileSize - 2 * outerPadding;
        Color bodyColor = (piece.getPlayer() == Player.PLAYER1)
                ? new Color(30, 30, 30)
                : new Color(245, 245, 245);
        Color outlineColor = (piece.getPlayer() == Player.PLAYER1)
                ? new Color(220, 220, 220)
                : new Color(30, 30, 30);

        g2d.setColor(bodyColor);
        g2d.fillOval(drawX + outerPadding, drawY + outerPadding, outerDiameter, outerDiameter);
        g2d.setColor(outlineColor);
        g2d.setStroke(new BasicStroke(Math.max(1.5f, tileSize / 28f)));
        g2d.drawOval(drawX + outerPadding, drawY + outerPadding, outerDiameter, outerDiameter);

        int innerPadding = Math.max(outerPadding + 4, (tileSize * 3) / 10);
        int innerDiameter = Math.max(4, tileSize - 2 * innerPadding);
        g2d.setColor(ColorMapper.toAwtColor(piece.getColor()));
        g2d.fillOval(drawX + innerPadding, drawY + innerPadding, innerDiameter, innerDiameter);
        g2d.setColor(outlineColor);
        g2d.setStroke(new BasicStroke(Math.max(1f, tileSize / 48f)));
        g2d.drawOval(drawX + innerPadding, drawY + innerPadding, innerDiameter, innerDiameter);
    }
}
