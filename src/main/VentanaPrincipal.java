package main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

// --- ¡IMPORTS ACTUALIZADOS! ---
import motorJuego.Jugador;
import Vista.PanelJuego;   // Importa el Panel de la carpeta 'Vista'
import motorJuego.LogicaJuego;

public class VentanaPrincipal extends JFrame {

    // Sigue siendo 'vista.PanelJuego'
    private PanelJuego panelJuego;

    private JLabel partidasJugador1;
    private JLabel partidasJugador2;
    private JLabel nombreJugador1;
    private JLabel nombreJugador2;
    private JLabel etiquetaTiempo; // Para el cronómetro

    private final Color colorActivo = Color.YELLOW;
    private final Color colorInactivo = Color.BLACK;

    private static int partidasGanadasJ1 = 0;
    private static int partidasGanadasJ2 = 0;

    public VentanaPrincipal() {
        setTitle("Tetris Balance");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 0));

        // Crea la Vista (que a su vez crea el Modelo)
        panelJuego = new PanelJuego();
        add(panelJuego, BorderLayout.CENTER);

        JPanel panelInfo = crearPanelInfo();
        add(panelInfo, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        Timer timer = new Timer(100, e -> actualizarUI());
        timer.start();
    }

    private JPanel crearPanelInfo() {
        // ... (Tu código exacto para crearPanelInfo) ...
        // (Este código ya está bien, lo incluyo por completitud)

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(55, 47, 47));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        panel.setPreferredSize(new Dimension(200, 100));

        Dimension scoreBoxSize = new Dimension(120, 50);
        Dimension timeBoxSize = new Dimension(140, 60);

        // --- Panel de Tiempo ---
        JPanel panelTiempo = new JPanel(new GridBagLayout());
        panelTiempo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.ORANGE), "TIEMPO", TitledBorder.CENTER, TitledBorder.TOP
        ));
        etiquetaTiempo = new JLabel("00:00"); // Valor inicial
        etiquetaTiempo.setFont(new Font("Monospaced", Font.BOLD, 28));
        etiquetaTiempo.setForeground(Color.RED);
        panelTiempo.setPreferredSize(timeBoxSize);
        panelTiempo.setMaximumSize(timeBoxSize);
        panelTiempo.setMinimumSize(timeBoxSize);
        panelTiempo.add(etiquetaTiempo);

        // --- motorJuego.Jugador 1 ---
        nombreJugador1 = new JLabel("PLAYER 1");
        nombreJugador1.setFont(new Font("Arial", Font.BOLD, 20));
        nombreJugador1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panelPartidasJ1 = new JPanel(new GridBagLayout());
        panelPartidasJ1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "PARTIDAS", TitledBorder.CENTER, TitledBorder.TOP
        ));
        partidasJugador1 = new JLabel(String.valueOf(partidasGanadasJ1));
        partidasJugador1.setFont(new Font("Monospaced", Font.BOLD, 24));
        panelPartidasJ1.setPreferredSize(scoreBoxSize);
        panelPartidasJ1.setMaximumSize(scoreBoxSize);
        panelPartidasJ1.setMinimumSize(scoreBoxSize);
        panelPartidasJ1.add(partidasJugador1);

        // --- motorJuego.Jugador 2 ---
        nombreJugador2 = new JLabel("PLAYER 2");
        nombreJugador2.setFont(new Font("Arial", Font.BOLD, 20));
        nombreJugador2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel panelPartidasJ2 = new JPanel(new GridBagLayout());
        panelPartidasJ2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "PARTIDAS", TitledBorder.CENTER, TitledBorder.TOP
        ));
        partidasJugador2 = new JLabel(String.valueOf(partidasGanadasJ2));
        partidasJugador2.setFont(new Font("Monospaced", Font.BOLD, 24));
        panelPartidasJ2.setPreferredSize(scoreBoxSize);
        panelPartidasJ2.setMaximumSize(scoreBoxSize);
        panelPartidasJ2.setMinimumSize(scoreBoxSize);
        panelPartidasJ2.add(partidasJugador2);

        // --- Añadir todo al panel ---
        panel.add(panelTiempo);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(nombreJugador1);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(panelPartidasJ1);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(nombreJugador2);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(panelPartidasJ2);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /**
     * ACTUALIZADO para hablar con el Motor a través del Panel
     */
    private void actualizarUI() {
        if (panelJuego != null && panelJuego.getMotor() != null) {

            // ¡AHORA SE ACCEDE ASÍ!
            LogicaJuego motor = panelJuego.getMotor();

            // Actualizar el temporizador
            int segundos = motor.getSegundosTranscurridos();
            int min = segundos / 60;
            int sec = segundos % 60;
            etiquetaTiempo.setText(String.format("%02d:%02d", min, sec));

            // Actualiza el resaltado del jugador activo
            Jugador jugadorActual = motor.getManejoTurnos().getJugadorActual();

            if (jugadorActual == motor.getJugador1()) {
                nombreJugador1.setForeground(colorActivo);
                nombreJugador2.setForeground(colorInactivo);
            } else {
                nombreJugador1.setForeground(colorInactivo);
                nombreJugador2.setForeground(colorActivo);
            }
        }
    }

    // --- Métodos estáticos (sin cambios) ---
    public static void jugador1Gano() {
        partidasGanadasJ1++;
    }
    public static void jugador2Gano() {
        partidasGanadasJ2++;
    }
    public static void resetPartidas() {
        partidasGanadasJ1 = 0;
        partidasGanadasJ2 = 0;
    }
}