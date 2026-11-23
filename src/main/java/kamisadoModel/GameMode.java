package kamisadoModel;

/** Supported play modes for Kamisado. */
public enum GameMode {
    PVP("Egymás ellen"),
    AI_EASY("Gép ellen (Könnyű)"),
    AI_HARD("Gép ellen (Nehéz)");

    private final String displayName;

    GameMode(String displayName) {
        this.displayName = displayName;
    }

    public boolean isAiEnabled() {
        return this != PVP;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
