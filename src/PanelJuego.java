import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;
import javax.swing.*;

import Bloques.*;

public class PanelJuego extends JPanel {
    //Definición del panel de juego
    private static final int anchoPanel = 400;
    private static final int altoPanel = 600;
    private static final int tamanioBloque = 20;
    private static final int anchoTablero = anchoPanel / tamanioBloque;
    private static final int altoTablero = altoPanel / tamanioBloque;

    private Color[][] tablero; // guarda estado de celdas (null = vacio)

    private PiezaPadre piezaActual;
    private Random piezaAleatoria = new Random();

    private boolean moverIzq = false;
    private boolean moverDer = false;
    private boolean moverArriba = false;
    private boolean moverAbajo = false;
    private boolean rotar = false;

    private int[] pesoColumnas = new int[anchoTablero];

    private double anguloInclinacion = 0.0;  // Ángulo en radianes para la inclinación visual

    private static final double inclinacionPermitido = 0.20;
    private boolean finDelJuego = false;
    private boolean dialogoMostrado = false; // Para evitar mostrar el diálogo múltiples veces

    //jugadores
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador jugadorActual;

    private Timer gameTimer; // Referencia al timer del juego


    /* ------------------ Constructor ---------------------------*/
    public PanelJuego() {
        setPreferredSize(new Dimension(anchoPanel, altoPanel));
        setBackground(Color.BLACK);

        tablero = new Color[altoTablero][anchoTablero];
        inicializarPlataforma();

        for (int i = 0; i < pesoColumnas.length; i++) {
            pesoColumnas[i] = 0;
        }

        jugador1 = new Jugador("Jugador 1");
        jugador2 = new Jugador("Jugador 2");

        jugadorActual = jugador1;
        piezaActual = null;
        generarNuevaPiezaParaJugador(jugadorActual);

        setFocusable(true);
        requestFocusInWindow(); // necesario para recibir eventos en teclado
        controlesTeclado();
        generarNuevaPiezaParaJugador(jugadorActual);

        gameTimer = new Timer(50, e -> actualizarMovimiento());
        gameTimer.start();
    }


    // Inicializa la plataforma fija en el fondo
    private void inicializarPlataforma() {
        int[][] formaPlataforma = {
                {1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        int anchoPlataformaEnBloques = formaPlataforma[0].length;
        int altoPlataformaEnBloques = formaPlataforma.length;
        int columnaInicial = (anchoTablero - anchoPlataformaEnBloques) / 2;
        int filaInicial = altoTablero - altoPlataformaEnBloques;

        for (int fila = 0; fila < altoPlataformaEnBloques; fila++) {
            for (int columna = 0; columna < anchoPlataformaEnBloques; columna++) {
                if (formaPlataforma[fila][columna] == 1) {
                    tablero[filaInicial + fila][columnaInicial + columna] = new Color(141, 50, 50);
                }
            }
        }
    }

    // Genera una nueva pieza (por ahora solo PiezaI y PiezaJ)
    private void generarNuevaPiezaParaJugador(Jugador jugador) {
        int indice = piezaAleatoria.nextInt(7);
        PiezaPadre piezaNueva = null;
        switch (indice) {
            case 0:
                piezaNueva = new PiezaI();
                break;
            case 1:
                piezaNueva = new PiezaJ();
                break;
            case 2:
                piezaNueva = new PiezaL();
                break;
            case 3:
                piezaNueva = new PiezaO();
                break;
            case 4:
                piezaNueva = new PiezaS();
                break;
            case 5:
                piezaNueva = new PiezaT();
                break;
            case 6:
                piezaNueva = new PiezaZ();
                break;
        }
        piezaNueva.x = (anchoTablero - piezaNueva.getAncho()) / 2;
        piezaNueva.y = 0;
        jugador.setPiezaActual(piezaNueva);
        if (jugador == jugadorActual) {
            piezaActual = piezaNueva;
        }
        if (hayColision(piezaNueva.x, piezaNueva.y)) {
            finDelJuego = true;
        }
    }



    private void dibujarBloqueConBorde(Graphics2D g2d, int x, int y, Color color) {
        g2d.setColor(color);
        g2d.fillRect(x, y, tamanioBloque, tamanioBloque);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, tamanioBloque, tamanioBloque);
    }


    /*------------------ Dibuja la pieza y plataforma -----------------------*/
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Guardamos la transformación original para restaurar luego
        AffineTransform original = g2d.getTransform();

        // Punto de pivote: centro abajo del panel (como una balanza)
        int pivotX = anchoPanel / 2;
        int pivotY = altoPanel;

        // Aplicar rotación para simular inclinación
        g2d.rotate(anguloInclinacion, pivotX, pivotY);

        // Dibujo de la plataforma bloque por bloque con borde negro
        for (int fila = 0; fila < altoTablero; fila++) {
            for (int columna = 0; columna < anchoTablero; columna++) {
                Color colorCelda = tablero[fila][columna];
                if (colorCelda != null) {
                    int x = columna * tamanioBloque;
                    int y = fila * tamanioBloque;
                    dibujarBloqueConBorde(g2d, x, y, colorCelda);
                }
            }
        }

        // Dibujo de la pieza actual con borde negro
        if (piezaActual != null) {
            g2d.setColor(piezaActual.getColor());
            int[][] forma = piezaActual.getForma();

            for (int fila = 0; fila < forma.length; fila++) {
                for (int columna = 0; columna < forma[fila].length; columna++) {
                    if (forma[fila][columna] == 1) {
                        int posicionX = (piezaActual.x + columna) * tamanioBloque;
                        int posicionY = (piezaActual.y + fila) * tamanioBloque;

                        dibujarBloqueConBorde(g2d, posicionX, posicionY, piezaActual.getColor());
                    }
                }
            }
        }

        // Restaurar transformacion para que la cuadrícula no rote
        g2d.setTransform(original);

        //Si el juego terminó, mostrar ventana emergente
        if (finDelJuego && !dialogoMostrado) {
            dialogoMostrado = true;
            gameTimer.stop(); // Detener el timer del juego
            
            // Mostrar diálogo en el hilo de eventos
            SwingUtilities.invokeLater(() -> {
                mostrarDialogoGameOver();
            });
        }
    }

    // ------ Metodo para dibujar una pieza
    private void dibujarPieza(Graphics2D g2d, PiezaPadre pieza){
        g2d.setColor(pieza.getColor());
        int[][] forma = pieza.getForma();

        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    int posicionX = (pieza.x + columna) * tamanioBloque;
                    int posicionY = (pieza.y + fila) * tamanioBloque;
                    dibujarBloqueConBorde(g2d, posicionX, posicionY, pieza.getColor());
                }
            }
        }
    }

    /*------------------ Ventana emergente de Fin de juego -----------------------*/
    private void mostrarDialogoGameOver() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        // Crear diálogo personalizado
        JDialog dialog = new JDialog(parent, "Fin del juego", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior con título y puntuaciones
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(40, 40, 40));
        
        JLabel titleLabel = new JLabel("Fin del juego");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel ganador = new JLabel();

        if (jugadorActual==jugador2){
            ganador.setText("¡El jugador 2 ganó!");
        } else if (jugadorActual==jugador1){
            ganador.setText("¡El jugador 1 ganó!");
        }

        ganador.setFont(new Font("Arial", Font.BOLD, 20));
        ganador.setForeground(Color.WHITE);
        ganador.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(titleLabel);
        topPanel.add(ganador);
        topPanel.add(Box.createVerticalStrut(30));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(40, 40, 40));
        
        // Botón Reintentar
        JButton btnReintentar = createStyledButton("Reintentar", new Color(46, 204, 113));
        btnReintentar.addActionListener(e -> {
            for (Window window : Window.getWindows()) {
                if (window.isShowing()) {
                window.dispose();
                }
            }
            gameTimer.restart();

            new VentanaPrincipal();
        });
        
        // Botón Salir
        JButton btnSalir = createStyledButton("Salir", new Color(231, 76, 60));
        btnSalir.addActionListener(e -> {
            for (Window window : Window.getWindows()) {
                if (window.isShowing()) {
                window.dispose();
                }
            }
            gameTimer.restart();

            new MenuUI();
        });
        
        buttonPanel.add(btnReintentar);
        buttonPanel.add(btnSalir);
        
        // Ensamblar todo
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    // Método para crear botones estilizados
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
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

    /*------------------ Movimientos por teclado -----------------------*/
    private void controlesTeclado() {
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int key = e.getKeyCode();
                if (key == java.awt.event.KeyEvent.VK_LEFT) moverIzq = true;
                if (key == java.awt.event.KeyEvent.VK_RIGHT) moverDer = true;
                if (key == java.awt.event.KeyEvent.VK_UP) moverArriba = true;
                if (key == java.awt.event.KeyEvent.VK_DOWN) moverAbajo = true;
                if (key == java.awt.event.KeyEvent.VK_SPACE) rotar = true;
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                int key = e.getKeyCode();
                if (key == java.awt.event.KeyEvent.VK_LEFT) moverIzq = false;
                if (key == java.awt.event.KeyEvent.VK_RIGHT) moverDer = false;
                if (key == java.awt.event.KeyEvent.VK_UP) moverArriba = false;
                if (key == java.awt.event.KeyEvent.VK_DOWN) moverAbajo = false;
                if (key == java.awt.event.KeyEvent.VK_SPACE) rotar = false;
            }
        });
    }

    private void actualizarMovimiento() {
        if (finDelJuego) {
            return;
        }

        PiezaPadre piezaActual = jugadorActual.getPiezaActual();
        if (piezaActual == null) return;

        boolean puedeMoverIzquierda = !hayColision(piezaActual.x - 1, piezaActual.y);
        boolean puedeMoverDerecha = !hayColision(piezaActual.x + 1, piezaActual.y);
        boolean puedeMoverAbajo = !hayColision(piezaActual.x, piezaActual.y + 1);

        if (moverIzq && piezaActual.x > 0 && puedeMoverIzquierda) {
            piezaActual.moverIzq();
        }

        if (moverDer && piezaActual.x + piezaActual.getAncho() < anchoTablero && puedeMoverDerecha) {
            piezaActual.moverDer();
        }

        if (moverAbajo){
            if(puedeMoverAbajo){
                piezaActual.moverAbajo();
            }else {
                if(piezaTocaSuelo(piezaActual)){
                    finDelJuego = true;
                } else {
                    fijarPieza();
                }
            }
        }


        if (moverArriba && piezaActual.y > 0 && !hayColision(piezaActual.x, piezaActual.y - 1)) {
            piezaActual.y--;
        }

        if (rotar) {
            piezaActual.rotar();
            if(hayColision(piezaActual.x, piezaActual.y)){
                piezaActual.rotar();
            }
            rotar = false;
        }

        repaint();
    }

    /*---- Metodo para detectar si la pieza colisiono con el suelo --*/
    private boolean piezaTocaSuelo(PiezaPadre pieza){
        int[][] forma = pieza.getForma();
        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    int yRelativo = pieza.y + fila;
                    if (yRelativo == altoTablero -1){
                        return true;
                    }
                }
            }
        }
        return false;
    }



    /* ---------------- Metodo encargado de detectar las colisiones --------------------------- */
    private boolean hayColision(int colisionX, int colisionY) {
        if (piezaActual == null) return false;

        int[][] forma = piezaActual.getForma();
        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    int xRelativo = colisionX + columna;
                    int yRelativo = colisionY + fila;

                    // Comprueba límites
                    if (xRelativo < 0 || xRelativo >= anchoTablero || yRelativo >= altoTablero) {
                        return true;
                    }
                    // Comprueba colisión con bloque
                    if (tablero[yRelativo][xRelativo] != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* ------------------------ Metodo para fijar la pieza ----------------------- */
    private void fijarPieza() {
        PiezaPadre piezaActual = jugadorActual.getPiezaActual();
        if (piezaActual == null) return;
        int[][] forma = piezaActual.getForma();
        int pesoPieza = piezaActual.getPeso();

        Color color = piezaActual.getColor();
        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    int xRelativo = piezaActual.x + columna;
                    int yRelativo = piezaActual.y + fila;

                    if (yRelativo >= 0 && yRelativo < altoTablero && xRelativo >= 0 && xRelativo < anchoTablero) {
                        tablero[yRelativo][xRelativo] = color;
                        pesoColumnas[xRelativo] += pesoPieza;
                    }
                }
            }
        }
        jugadorActual.sumarPuntos(10);
        inclinarPlataforma();
        jugadorActual.setPiezaActual(null);
        cambiarTurno();
        generarNuevaPiezaParaJugador(jugadorActual);
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    // Metodo cambio de turno
    private void cambiarTurno(){
        if(jugadorActual == jugador1){
            jugadorActual = jugador2;
        }else{
            jugadorActual = jugador1;
        }
    }

    private void inclinarPlataforma() {
        int pesoIzq = 0;
        int pesoDer = 0;
        int centro = anchoTablero / 2;

        for (int i = 0; i< centro;i++) {
            pesoIzq += pesoColumnas[i];
        }
        for (int i = centro; i< anchoTablero;i++) {
            pesoDer += pesoColumnas[i];
        }

        int diferenciaPeso = pesoDer - pesoIzq;

        anguloInclinacion = diferenciaPeso * 0.01;

        if (anguloInclinacion > 0.2) {
            anguloInclinacion = 0.2;
        }
        if (anguloInclinacion < -0.2) {
            anguloInclinacion = -0.2;
        }

        if (anguloInclinacion >= inclinacionPermitido){
            finDelJuego = true;
        } else if(anguloInclinacion <= -inclinacionPermitido) {
            finDelJuego = true;
        }
    }
}