package kamisadoView;

import java.awt.Color;

/** Maps {@link kamisadoModel.Color} values to AWT colors and Hungarian display names. */
public final class ColorMapper {

    private ColorMapper() {
    }

    public static Color toAwtColor(kamisadoModel.Color color) {
        if (color == null) {
            return new Color(220, 220, 220);
        }
        return switch (color) {
            case ORANGE -> new Color(243, 138, 36);
            case BLUE -> new Color(41, 121, 214);
            case PURPLE -> new Color(142, 68, 173);
            case PINK -> new Color(232, 108, 165);
            case YELLOW -> new Color(244, 208, 63);
            case RED -> new Color(214, 58, 58);
            case GREEN -> new Color(46, 164, 79);
            case BROWN -> new Color(121, 74, 46);
        };
    }

    public static String toDisplayName(kamisadoModel.Color color) {
        if (color == null) {
            return "Bármelyik";
        }
        return switch (color) {
            case ORANGE -> "Narancs";
            case BLUE -> "Kék";
            case PURPLE -> "Lila";
            case PINK -> "Rózsaszín";
            case YELLOW -> "Sárga";
            case RED -> "Piros";
            case GREEN -> "Zöld";
            case BROWN -> "Barna";
        };
    }
}
