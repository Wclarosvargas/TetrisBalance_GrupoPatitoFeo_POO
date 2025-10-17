import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;
import javax.swing.Timer;

import Bloques.*;

public class PanelJuego extends JPanel {
    //Definición del panel de juego
    private static final int anchoPanel = 300;
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

    /* ------------------ Constructor ---------------------------*/
    public PanelJuego() {
        setPreferredSize(new Dimension(anchoPanel, altoPanel));
        setBackground(Color.BLACK);

        tablero = new Color[altoTablero][anchoTablero];
        inicializarPlataforma();

        for (int i = 0; i < pesoColumnas.length; i++) {
            pesoColumnas[i] = 0;
        }

        setFocusable(true);
        requestFocusInWindow(); // necesario para recibir eventos en teclado
        controlesTeclado();
        generarNuevaPieza();

        new Timer(50, e -> actualizarMovimiento()).start();
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

    // Genera una nueva pieza (por ahora solo Bloques.PiezaI)
    private void generarNuevaPieza() {
        int indice = piezaAleatoria.nextInt(1);
        switch (indice) {
            case 0:
                piezaActual = new PiezaI();
                break;
        }
        piezaActual.x = (anchoTablero - piezaActual.getAncho()) / 2;
        piezaActual.y = 0;
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

        // Dibujo de la plataforma bloque por bloque
        for (int fila = 0; fila < altoTablero; fila++) {
            for (int columna = 0; columna < anchoTablero; columna++) {
                Color colorCelda = tablero[fila][columna];
                if (colorCelda != null) {
                    g2d.setColor(colorCelda);
                    int x = columna * tamanioBloque;
                    int y = fila * tamanioBloque;
                    g2d.fillRect(x, y, tamanioBloque, tamanioBloque);
                }
            }
        }

        // Dibujo de la pieza actual
        if (piezaActual != null) {
            g2d.setColor(piezaActual.getColor());
            int[][] forma = piezaActual.getForma();

            for (int fila = 0; fila < forma.length; fila++) {
                for (int columna = 0; columna < forma[fila].length; columna++) {
                    if (forma[fila][columna] == 1) {
                        int posicionX = (piezaActual.x + columna) * tamanioBloque;
                        int posicionY = (piezaActual.y + fila) * tamanioBloque;
                        g2d.fillRect(posicionX, posicionY, tamanioBloque, tamanioBloque);
                    }
                }
            }
        }

        // Restaurar transformacion para que la cuadrícula no rote
        g2d.setTransform(original);

        // Dibujo de la cuadrícula del tablero (sin rotar)
        g2d.setColor(Color.lightGray);
        for (int fila = 0; fila < altoTablero; fila++) {
            for (int columna = 0; columna < anchoTablero; columna++) {
                int x = columna * tamanioBloque;
                int y = fila * tamanioBloque;
                g2d.drawRect(x, y, tamanioBloque, tamanioBloque);
            }
        }
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
        if (piezaActual == null) return;

        if (moverIzq && piezaActual.x > 0) {
            piezaActual.moverIzq();
        }

        if (moverDer && piezaActual.x + piezaActual.getAncho() < anchoTablero) {
            piezaActual.moverDer();
        }

        if (moverAbajo && piezaActual.y + piezaActual.getForma().length < altoTablero) {
            if (!hayColision(piezaActual.x, piezaActual.y + 1)) {
                piezaActual.moverAbajo();
            } else {
                fijarPieza();
                generarNuevaPieza();
            }
        }

        if (moverArriba && piezaActual.y > 0) {
            piezaActual.y--;
        }

        if (rotar) {
            piezaActual.rotar();
            rotar = false;
        }

        repaint();
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
                        return false;
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
        inclinarPlataforma();
    }

    private void inclinarPlataforma() {
        int pesoIzq = 0;
        int pesoDer = 0;
        int centro = anchoTablero / 2;

        for (int i = 0; i < centro; i++) pesoIzq += pesoColumnas[i];
        for (int i = centro; i < anchoTablero; i++) pesoDer += pesoColumnas[i];

        int diferenciaPeso = pesoDer - pesoIzq;

        // Limitar ángulo entre -0.2 y 0.2 rad (aprox -11.5° a 11.5°)
        anguloInclinacion = Math.max(-0.2, Math.min(0.2, diferenciaPeso * 0.01));
    }
}
