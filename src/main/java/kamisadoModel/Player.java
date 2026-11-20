package kamisadoModel;

public enum Player {
    PLAYER1, PLAYER2;

    private boolean isWinner = false;

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }
}
