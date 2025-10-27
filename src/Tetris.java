import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Tetris extends JFrame {
    public Tetris() {
        try{
            PanelJuego panel = new PanelJuego();
            add(panel);

            setTitle("Tetris Balance");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
    }catch(Exception error){
        error.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this, "Error al iniciar Tetris Balance:\n"+error.getMessage(),"Error en la ventana",javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MenuUI();
            } catch (Exception error) {
                error.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        "Error al iniciar el juego:\n" + error.getMessage(),
                        "Error fatal",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
