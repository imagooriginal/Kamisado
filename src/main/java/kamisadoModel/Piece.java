package kamisadoModel;

public class Piece {
    private final Color color;
    private Square square;
    private Player player;

    public Piece(Color color, Square square, Player player) {
        this.color = color;
        this.square = square;
        this.player = player;

    }

    public Color getColor() {
        return color;
    }

    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        this.square = square;
    }

    public Player getPlayer() {
        return player;
    }

}
