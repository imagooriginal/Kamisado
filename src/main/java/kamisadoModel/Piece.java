package kamisadoModel;

public class Piece {
    private final Color color;
    private final Square square;

    public Piece(Color color, Square square) {
        this.color = color;
        this.square = square;
    }

    public Color getColor() {
        return color;
    }

    public Square getSquare() {
        return square;
    }

}
