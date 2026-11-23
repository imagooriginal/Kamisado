package kamisadoModel;

import javax.swing.SwingUtilities;
import kamisadoView.KamisadoFrame;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KamisadoFrame().setVisible(true));
    }
}
