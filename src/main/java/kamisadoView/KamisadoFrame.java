package kamisadoView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import kamisadoModel.Board;
import kamisadoModel.Game;
import kamisadoModel.GameMode;
import kamisadoModel.KamisadoAI;
import kamisadoModel.Move;
import kamisadoModel.Player;

/** Main Swing window hosting the Kamisado status bar, mode selector, board panel, and AI turn scheduling. */
public class KamisadoFrame extends JFrame {

    private static final int AI_MOVE_DELAY_MS = 350;

    private transient Board board;
    private transient Game game;
    private final transient KamisadoAI ai = new KamisadoAI();
    private GameMode gameMode = GameMode.PVP;

    private final BoardPanel boardPanel;
    private final JLabel statusLabel;
    private final JLabel colorLabel;
    private final JPanel colorSwatch;
    private final JComboBox<GameMode> modeSelector;
    private final JButton fullscreenButton;
    private Timer aiTimer;
    private boolean gameOverDialogShown = false;

    public KamisadoFrame() {
        super("Kamisado");
        this.board = new Board();
        this.game = new Game();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setResizable(true);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        colorSwatch = new JPanel();
        colorSwatch.setPreferredSize(new Dimension(20, 20));
        colorSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        colorLabel = new JLabel();
        colorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        modeSelector = new JComboBox<>(GameMode.values());
        modeSelector.setSelectedItem(gameMode);
        modeSelector.addActionListener(e -> startNewGame());

        JButton newGameButton = new JButton("Új játék");
        newGameButton.addActionListener(e -> startNewGame());

        fullscreenButton = new JButton("Teljes képernyő");
        fullscreenButton.addActionListener(e -> toggleFullscreen());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        topPanel.add(statusLabel);
        topPanel.add(colorSwatch);
        topPanel.add(colorLabel);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        controlPanel.add(new JLabel("Játékmód:"));
        controlPanel.add(modeSelector);
        controlPanel.add(newGameButton);
        controlPanel.add(fullscreenButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(controlPanel, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);

        boardPanel = new BoardPanel(board, game, this::onGameStateChanged);

        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        boardWrapper.add(boardPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(boardWrapper, BorderLayout.CENTER);

        addWindowStateListener(e -> updateFullscreenButtonText());

        updateStatusBar();
        pack();
        setMinimumSize(new Dimension(520, 620));
        setLocationRelativeTo(null);
    }

    private void toggleFullscreen() {
        boolean maximized = (getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
        setExtendedState(maximized ? NORMAL : MAXIMIZED_BOTH);
        updateFullscreenButtonText();
    }

    private void updateFullscreenButtonText() {
        boolean maximized = (getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
        fullscreenButton.setText(maximized ? "Normál méret" : "Teljes képernyő");
    }

    private void startNewGame() {
        stopAiTimer();
        GameMode selected = (GameMode) modeSelector.getSelectedItem();
        gameMode = (selected != null) ? selected : GameMode.PVP;
        board = new Board();
        game = new Game();
        gameOverDialogShown = false;
        boardPanel.reset(board, game);
        updateStatusBar();
        scheduleAiMoveIfNeeded();
    }

    private void onGameStateChanged() {
        updateStatusBar();
        if (game.isGameOver()) {
            stopAiTimer();
            boardPanel.setInputEnabled(false);
            showGameOverDialogOnce();
            return;
        }
        scheduleAiMoveIfNeeded();
    }

    private void scheduleAiMoveIfNeeded() {
        if (!isAiTurn()) {
            boardPanel.setInputEnabled(true);
            return;
        }
        boardPanel.setInputEnabled(false);
        stopAiTimer();
        aiTimer = new Timer(AI_MOVE_DELAY_MS, e -> performAiMove());
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    private void performAiMove() {
        if (!isAiTurn()) {
            boardPanel.setInputEnabled(true);
            return;
        }
        Move move = ai.chooseMove(board, game, gameMode);
        if (move != null) {
            game.move(board, Player.PLAYER2, move.from(), move.to());
            boardPanel.refresh();
            onGameStateChanged();
        }
    }

    private boolean isAiTurn() {
        return gameMode.isAiEnabled()
                && !game.isGameOver()
                && game.getCurrentPlayer() == Player.PLAYER2;
    }

    private void stopAiTimer() {
        if (aiTimer != null && aiTimer.isRunning()) {
            aiTimer.stop();
        }
    }

    private void showGameOverDialogOnce() {
        if (gameOverDialogShown) {
            return;
        }
        gameOverDialogShown = true;
        String winnerName = formatPlayerName(game.getWinner());
        JOptionPane.showMessageDialog(
                this,
                "Vége a játéknak! A győztes: " + winnerName,
                "Játék vége",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStatusBar() {
        if (game.isGameOver()) {
            statusLabel.setText("Vége a játéknak! Győztes: " + formatPlayerName(game.getWinner()));
            colorSwatch.setVisible(false);
            colorLabel.setText("");
            return;
        }

        statusLabel.setText("Soron van: " + formatPlayerName(game.getCurrentPlayer()));
        kamisadoModel.Color nextColor = game.getNextMoveColor();
        colorSwatch.setVisible(nextColor != null);
        colorSwatch.setBackground(ColorMapper.toAwtColor(nextColor));
        colorLabel.setText("Kötelező szín: " + ColorMapper.toDisplayName(nextColor));
    }

    private String formatPlayerName(Player player) {
        if (player == Player.PLAYER1) {
            return gameMode.isAiEnabled() ? "Te (Fekete)" : "1. játékos (Fekete)";
        }
        if (player == Player.PLAYER2) {
            return gameMode.isAiEnabled() ? "Gép (Fehér)" : "2. játékos (Fehér)";
        }
        return "Ismeretlen";
    }
}
