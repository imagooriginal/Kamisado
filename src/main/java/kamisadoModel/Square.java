package kamisadoModel;

public class Square {
    private final Color color;
    private final Coordinates coordinates;
    private boolean isOccupied;
    private Piece piece;

    public Square(Color color, Coordinates coordinates) {
        this.color = color;
        this.coordinates = coordinates;
        this.isOccupied = false;
        this.piece = null;
    }

    public Color getColor() {
        return color;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}
