package vista; // <-- ¡NUEVA LÍNEA DE PAQUETE!

// --- IMPORTS NECESARIOS ---
import main.VentanaPrincipal;
import motorJuego.LogicaJuego;     // Importa el Modelo
import motorJuego.Jugador;        // Importa motorJuego.Jugador (para el diálogo)
import Bloques.PiezaPadre;   // Importa PiezaPadre (para dibujar)
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import main.MenuUI;


public class PanelJuego extends JPanel {
    private LogicaJuego motor;

    // --- CONSTANTES Y VARIABLES DE LA VISTA ---
    private static final int anchoPanel = 400;
    private static final int altoPanel = 600;
    private static final int tamanioBloque = 20;
    private Image imagenFondo;
    private boolean dialogoMostrado = false;

    /* ------------------ Constructor de la Vista ---------------------------*/
    public PanelJuego() {
        motor = new LogicaJuego();

        motor.setPanel(this);

        setPreferredSize(new Dimension(anchoPanel, altoPanel));
        try {
            imagenFondo = new ImageIcon(getClass().getResource("/resources/Fondo_Juego.jpg")).getImage();
        } catch (Exception errorImagen) {
            errorImagen.printStackTrace();
            System.err.println("Error al cargar la imagen de fondo. Se usara el color negro");
            imagenFondo = null;
            setBackground(Color.BLACK);
        }
        setFocusable(true);
        requestFocusInWindow();
        controlesTeclado();
    }

    public LogicaJuego getMotor() {
        return motor;
    }


    private void controlesTeclado() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                motor.procesarTeclaPresionada(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                motor.procesarTeclaLiberada(e.getKeyCode());
            }
        });
    }

    /*------------------ Dibuja la pieza y plataforma -----------------------*/
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Dibuja el fondo
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        //  Aplicar rotación
        AffineTransform original = g2d.getTransform();
        int pivotX = anchoPanel / 2;
        int pivotY = altoPanel;
        g2d.rotate(motor.getAnguloInclinacion(), pivotX, pivotY); // <-- Pide al Motor

        // Dibujar plataforma (pidiendo el tablero al motor)
        Color[][] tablero = motor.getTablero(); // <-- Pide al Motor
        for (int fila = 0; fila < tablero.length; fila++) {
            for (int columna = 0; columna < tablero[fila].length; columna++) {
                Color colorCelda = tablero[fila][columna];
                if (colorCelda != null) {
                    dibujarBloqueConBorde(g2d, columna * tamanioBloque, fila * tamanioBloque, colorCelda);
                }
            }
        }

        // 4. Dibujar pieza actual (pidiendo la pieza al motor)
        PiezaPadre piezaActual = motor.getPiezaActual(); // <-- Pide al Motor
        if (piezaActual != null) {
            g2d.setColor(piezaActual.getColor());
            int[][] forma = piezaActual.getForma();

            for (int fila = 0; fila < forma.length; fila++) {
                for (int columna = 0; columna < forma[fila].length; columna++) {
                    if (forma[fila][columna] == 1) {
                        int posicionX = (piezaActual.getX() + columna) * tamanioBloque;
                        int posicionY = (piezaActual.getY() + fila) * tamanioBloque;
                        dibujarBloqueConBorde(g2d, posicionX, posicionY, piezaActual.getColor());
                    }
                }
            }
        }

        // Restaura la transformación
        g2d.setTransform(original);

        // Comprueba si se debe mostrar el diálogo de fin de juego
        if (motor.isFinDelJuego() && !dialogoMostrado) {
            dialogoMostrado = true;

            // Mostrar diálogo en el hilo de eventos
            SwingUtilities.invokeLater(() -> {
                mostrarDialogoGameOver();
            });
        }
    }

    // Metodo de vista
    private void dibujarBloqueConBorde(Graphics2D g2d, int x, int y, Color color) {
        g2d.setColor(color);
        g2d.fillRect(x, y, tamanioBloque, tamanioBloque);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, tamanioBloque, tamanioBloque);
    }

    /*------------------ Ventana emergente de Fin de juego (VISTA) -----------------------*/
    private void mostrarDialogoGameOver() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(parent, "Fin del juego", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(40, 40, 40));

        JLabel titleLabel = new JLabel("Fin del juego");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Lógica de la Vista pidiendo datos al Motor ---
        Jugador jugadorganador = motor.calcularGanador(); // <-- Pide al Motor
        String textoGanador = "";

        if (jugadorganador == motor.getJugador1()) {
            textoGanador = "¡El jugador 1 ganó!";
            VentanaPrincipal.jugador1Gano();
        } else {
            textoGanador = "¡El jugador 2 ganó!";
            VentanaPrincipal.jugador2Gano();
        }

        // Le pide al Motor que guarde el historial
        motor.guardarHistorial(
                jugadorganador.getNombre(),
                motor.getRazonFinJuego(),
                motor.getSegundosTranscurridos()
        );

        JLabel labelGanador = new JLabel(textoGanador);
        labelGanador.setFont(new Font("Arial", Font.BOLD, 20));
        labelGanador.setForeground(Color.WHITE);
        labelGanador.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(titleLabel);
        topPanel.add(labelGanador);
        topPanel.add(Box.createVerticalStrut(30));

        // --- Botones (VISTA) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(40, 40, 40));

        JButton btnReintentar = createStyledButton("Reintentar", new Color(46, 204, 113));
        btnReintentar.addActionListener(e -> {
            for (Window window : Window.getWindows()) {
                if (window.isShowing()) window.dispose();
            }
            new VentanaPrincipal(); // El motor viejo se destruye, se crea uno nuevo
        });

        JButton btnSalir = createStyledButton("Salir", new Color(231, 76, 60));
        btnSalir.addActionListener(e -> {
            for (Window window : Window.getWindows()) {
                if (window.isShowing()) window.dispose();
            }
            VentanaPrincipal.resetPartidas();
            new MenuUI();
        });

        buttonPanel.add(btnReintentar);
        buttonPanel.add(btnSalir);

        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color originalColor = bgColor;
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(originalColor.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(originalColor);
            }
        });
        return btn;
    }
}