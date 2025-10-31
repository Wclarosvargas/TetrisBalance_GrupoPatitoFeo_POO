import main.MenuUI;

import javax.swing.SwingUtilities;

// (Asegúrate de que main.MenuUI.java esté en la raíz 'src/')
// import main.MenuUI; (No es necesario si está en el mismo paquete/raíz)

public class Tetris {

    public static void main(String[] args) {
        // Es una buena práctica iniciar Swing en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MenuUI().setVisible(true);
        });
    }
}