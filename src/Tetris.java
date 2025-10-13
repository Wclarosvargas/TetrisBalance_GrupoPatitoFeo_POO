import javax.swing.JFrame;
import java.awt.*;

public class Tetris extends JFrame {
    public Tetris() {
        PanelJuego panel = new PanelJuego();

        add(panel);

        setTitle("Tetris-Balance");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //encargado de cerrar el progrma.
        setResizable(false); //Evita el cambio de tamaño
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Tetris();

    }
}