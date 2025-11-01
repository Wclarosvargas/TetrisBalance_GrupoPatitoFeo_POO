import main.MenuUI;

import javax.swing.SwingUtilities;


public class Tetris {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuUI().setVisible(true);
        });
    }
}