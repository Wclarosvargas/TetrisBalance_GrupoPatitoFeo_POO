import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private PanelJuego panelJuego;
    private JLabel scoreJugador1;
    private JLabel scoreJugador2;
    private JPanel panelSiguientePiezaJ1;
    private JPanel panelSiguientePiezaJ2;

    public VentanaPrincipal() {
        setTitle("Tetris Balance");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,0));

        panelJuego = new PanelJuego();
        add(panelJuego, BorderLayout.CENTER);

        JPanel panelIzquierda = crearPanelJugador("Player 1");
        add(panelIzquierda, BorderLayout.WEST);

        JPanel panelDerecha = crearPanelJugador("Player 2");
        add(panelDerecha, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        Timer timer = new Timer(100, e -> actualizarUI());
        timer.start();
    }

    private JPanel crearPanelJugador(String nombreJugador) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(154, 147, 146));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel etiquetaNombre = new JLabel(nombreJugador);
        etiquetaNombre.setFont(new Font("Arial", Font.BOLD, 20));
        etiquetaNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelScore = new JPanel();
        panelScore.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "SCORE", TitledBorder.CENTER, TitledBorder.TOP
        ));
        JLabel etiquetaPuntaje = new JLabel("0");
        etiquetaPuntaje.setFont(new Font("Monospaced", Font.BOLD, 24));
        panelScore.add(etiquetaPuntaje);

        if (nombreJugador.equalsIgnoreCase("Player 1")) {
            scoreJugador1 = etiquetaPuntaje;
        } else {
            scoreJugador2 = etiquetaPuntaje;
        }

        JPanel panelSiguientePieza = new JPanel();
        panelSiguientePieza.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK), "NEXT", TitledBorder.CENTER, TitledBorder.TOP
        ));
        panelSiguientePieza.setPreferredSize(new Dimension(250, 150));

        if (nombreJugador.equalsIgnoreCase("Player 1")) {
            panelSiguientePiezaJ1 = panelSiguientePieza;
        } else {
            panelSiguientePiezaJ2 = panelSiguientePieza;
        }

        panel.add(etiquetaNombre);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(panelScore);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(panelSiguientePieza);

        return panel;
    }

    private void actualizarUI() {
        if (panelJuego != null) {
            scoreJugador1.setText(String.valueOf(panelJuego.getJugador1().getPuntaje()));
            scoreJugador2.setText(String.valueOf(panelJuego.getJugador2().getPuntaje()));
            // Aquí podrías actualizar también las piezas siguientes
        }
    }
}
