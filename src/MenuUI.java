import javax.swing.*;
import java.awt.*;

public class MenuUI extends Menu {

    public MenuUI() {
        super("Tetris Menu", 400, 600, "/resources/menu_bg.jpeg");
        setVisible(true);
    }

    @Override
    protected void inicializarComponentes(JPanel fondo) {
        // Panel del menú
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        menuPanel.setOpaque(false);
        menuPanel.setPreferredSize(new Dimension(250, 300));

        JLabel title = new JLabel("", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.RED);

        JButton startBtn = crearBoton("Iniciar juego");
        JButton helpBtn = crearBoton("Controles");
        JButton exitBtn = crearBoton("Salir");

        menuPanel.add(title);
        menuPanel.add(startBtn);
        menuPanel.add(helpBtn);
        menuPanel.add(exitBtn);

        // Agrega el menú centrado al fondo
        fondo.add(menuPanel);

        // Acciones de los botones
        startBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new VentanaPrincipal());
        });

        helpBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Controles:\n← → ↑ ↓ Mover\nEspacio: Rotar bloque"));

        exitBtn.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuUI::new);
    }
}
