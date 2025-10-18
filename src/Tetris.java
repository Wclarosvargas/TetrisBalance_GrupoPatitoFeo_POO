import javax.swing.JFrame;


public class Tetris extends JFrame {
    public Tetris() {
        PanelJuego panel = new PanelJuego();
        add(panel);

        setTitle("Tetris Balance");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        new Tetris();
    }
}