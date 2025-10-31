package motorJuego;

import Bloques.*;
import javax.swing.Timer; // Solo importamos Timer
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import Bloques.PiezaPadre;


public class LogicaJuego {
    private static final int anchoPanel = 400;
    private static final int altoPanel = 600;
    private static final int tamanioBloque = 20;
    private static final int anchoTablero = anchoPanel / tamanioBloque;
    private static final int altoTablero = altoPanel / tamanioBloque;
    private static final int velocidadGravedad = 500;
    private static final int velocidadRapida = 50;
    private static final double inclinacionPermitido = 0.20;

    //Estados del juego
    private Color[][] tablero;
    private PiezaPadre piezaActual;
    private ManejoTurnos manejoTurnos;
    private int[] pesoColumnas = new int[anchoTablero];
    private double anguloInclinacion = 0.0;
    private boolean finDelJuego = false;
    private String razonFinJuego = "";
    private int segundosTranscurridos;
    private Random piezaAleatoria = new Random();

    //Tiempos de Lógica
    private Timer gameTimer;
    private Timer gravityTimer;
    private Timer tiempoPartida;

    //Estado del controlador
    private boolean moverIzq = false;
    private boolean moverDer = false;
    private boolean moverAbajo = false;
    private boolean rotar = false;

    private javax.swing.JPanel panelRepintar;

    public LogicaJuego() {
        tablero = new Color[altoTablero][anchoTablero];
        inicializarPlataforma();

        for(int i = 0; i < pesoColumnas.length; i++) {
            pesoColumnas[i] = 0;
        }

        Jugador j1 = new Jugador("motorJuego.Jugador 1");
        Jugador j2 = new Jugador("motorJuego.Jugador 2");
        manejoTurnos = new ManejoTurnos(j1,j2);

        generarNuevaPiezaParaJugador(manejoTurnos.getJugadorActual());

        // Configuración de Timers
        gameTimer = new Timer(50, e -> actualizarMovimiento());
        gameTimer.start();

        gravityTimer = new Timer(velocidadGravedad, e -> aplicarGravedad());
        gravityTimer.start();

        segundosTranscurridos = 0;
        tiempoPartida = new Timer(1000, e ->{
            segundosTranscurridos++;
            if(panelRepintar != null){
                panelRepintar.repaint();
            }
        });
        tiempoPartida.start();
    }

    public void setPanel(javax.swing.JPanel panel) {
        this.panelRepintar = panel;
    }

    // ================ Metodos del juego ==========

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

        for(int fila = 0; fila<altoPlataformaEnBloques; fila++) {
            for (int columna = 0; columna<anchoPlataformaEnBloques; columna++) {
                if(formaPlataforma[fila][columna]==1) {
                    tablero[filaInicial + fila][columnaInicial + columna] = new Color(99, 68, 61);
                }
            }
        }
    }

    private void generarNuevaPiezaParaJugador(Jugador jugador) {
        try {
            int indice = piezaAleatoria.nextInt(7);
            PiezaPadre piezaNueva = null;
            if (indice == 0) piezaNueva = new PiezaI();
            else if (indice == 1) piezaNueva = new PiezaJ();
            else if (indice == 2) piezaNueva = new PiezaL();
            else if (indice == 3) piezaNueva = new PiezaO();
            else if (indice == 4) piezaNueva = new PiezaS();
            else if (indice == 5) piezaNueva = new PiezaT();
            else if (indice == 6) piezaNueva = new PiezaZ();

            if (piezaNueva == null) throw new IllegalStateException("No se pudo crear una nueva pieza");

            int xInicial = (anchoTablero - piezaNueva.getAncho()) / 2;
            int yInicial = 0;

            piezaNueva.setX(xInicial);
            piezaNueva.setY(yInicial);

            jugador.setPiezaActual(piezaNueva);
            if (jugador == manejoTurnos.getJugadorActual()) {
                piezaActual = piezaNueva;
            }
            // ¡USANDO GETTERS!
            if (hayColision(piezaNueva.getX(), piezaNueva.getY())) {
                terminarJuego("Bloqueo inicial");
            }
        } catch (Exception e) {
            e.printStackTrace();
            terminarJuego("Error de pieza");
        }
    }

    private void aplicarGravedad() {
        if (finDelJuego) return;
        if (piezaActual == null) return;

        // ¡USANDO GETTERS!
        boolean puedeMoverAbajo = !hayColision(piezaActual.getX(), piezaActual.getY() + 1);

        if (puedeMoverAbajo) {
            piezaActual.moverAbajo();
        } else {
            if (piezaTocaSuelo(piezaActual)) {
                terminarJuego("Fondo alcanzado");
            } else {
                fijarPieza();
            }
        }
        if (panelRepintar != null) panelRepintar.repaint(); // ¡Pide repintar!
    }

    private void actualizarMovimiento() {
        if (finDelJuego) return;
        if (piezaActual == null) return;

        // ¡USANDO GETTERS!
        int x = piezaActual.getX();
        int y = piezaActual.getY();

        boolean puedeMoverIzquierda = !hayColision(x - 1, y);
        boolean puedeMoverDerecha = !hayColision(x + 1, y);
        boolean puedeMoverAbajo = !hayColision(x, y + 1);

        if (moverIzq && x > 0 && puedeMoverIzquierda) {
            piezaActual.moverIzq();
        }
        if (moverDer && x + piezaActual.getAncho() < anchoTablero && puedeMoverDerecha) {
            piezaActual.moverDer();
        }
        if (moverAbajo) {
            if (puedeMoverAbajo) {
                piezaActual.moverAbajo();
            } else {
                if (piezaTocaSuelo(piezaActual)) {
                    terminarJuego("Fondo alcanzado (bajada rápida)");
                } else {
                    fijarPieza();
                }
            }
        }
        if (rotar) {
            piezaActual.rotar();
            // ¡USANDO GETTERS!
            if (hayColision(piezaActual.getX(), piezaActual.getY())) {
                piezaActual.rotar(); // Des-rotar
            }
            rotar = false;
        }
        if (panelRepintar != null) panelRepintar.repaint(); // ¡Pide repintar!
    }

    private boolean piezaTocaSuelo(PiezaPadre pieza) {
        int[][] forma = pieza.getForma();
        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    // ¡USANDO GETTER!
                    int yRelativo = pieza.getY() + fila;
                    if (yRelativo == altoTablero - 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hayColision(int colisionX, int colisionY) {
        if (piezaActual == null) return false;

        int[][] forma = piezaActual.getForma();
        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    int xRelativo = colisionX + columna;
                    int yRelativo = colisionY + fila;

                    if (xRelativo < 0 || xRelativo >= anchoTablero || yRelativo >= altoTablero) return true;
                    if (yRelativo < 0) continue; // Permite estar por encima del tablero
                    if (tablero[yRelativo][xRelativo] != null) return true;
                }
            }
        }
        return false;
    }

    private void fijarPieza() {
        if (piezaActual == null) return;

        int[][] forma = piezaActual.getForma();
        int pesoPieza = piezaActual.getPeso();
        Color color = piezaActual.getColor();

        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    // ¡USANDO GETTERS!
                    int xRelativo = piezaActual.getX() + columna;
                    int yRelativo = piezaActual.getY() + fila;

                    if (yRelativo >= 0 && yRelativo < altoTablero && xRelativo >= 0 && xRelativo < anchoTablero) {
                        tablero[yRelativo][xRelativo] = color;
                        pesoColumnas[xRelativo] += pesoPieza;
                    }
                }
            }
        }
        manejoTurnos.getJugadorActual().sumarPuntos(10);
        inclinarPlataforma();
        manejoTurnos.getJugadorActual().setPiezaActual(null);
        if (finDelJuego == false) {
            manejoTurnos.cambiarTurno();
            resetearTeclas();
        }
        generarNuevaPiezaParaJugador(manejoTurnos.getJugadorActual());
    }

    private void inclinarPlataforma() {
        int pesoIzq = 0;
        int pesoDer = 0;
        int centro = anchoTablero / 2;

        for (int i = 0; i < centro; i++) pesoIzq += pesoColumnas[i];
        for (int i = centro; i < anchoTablero; i++) pesoDer += pesoColumnas[i];

        int diferenciaPeso = pesoDer - pesoIzq;
        anguloInclinacion = diferenciaPeso * 0.01;

        if (anguloInclinacion > 0.2) anguloInclinacion = 0.2;
        if (anguloInclinacion < -0.2) anguloInclinacion = -0.2;

        if (anguloInclinacion >= inclinacionPermitido) {
            terminarJuego("Plataforma desequilibrada");
        } else if (anguloInclinacion <= -inclinacionPermitido) {
            terminarJuego("Plataforma desequilibrada");
        }
    }

    // --- MÉTODOS PÚBLICOS (CONTROLADOR Y PERSISTENCIA) ---

    public void terminarJuego(String razon) {
        if (finDelJuego) return;
        this.finDelJuego = true;
        this.razonFinJuego = razon;

        gameTimer.stop();
        gravityTimer.stop();
        tiempoPartida.stop();

        if (panelRepintar != null) panelRepintar.repaint();
    }

    public void guardarHistorial(String ganador, String razon, int duracionSegundos) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = dtf.format(LocalDateTime.now());
        String duracionFormateada = String.format("%02d:%02d", duracionSegundos / 60, duracionSegundos % 60);
        String lineaHistorial = String.format("[%s] - Ganador: %s - Razón: %s - Duración: %s\n",
                fechaHora, ganador, razon, duracionFormateada);

        try (FileWriter fw = new FileWriter("historial_partidas.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(lineaHistorial);
        } catch (IOException e) {
            System.err.println("Error al escribir en el historial: " + e.getMessage());
        }
    }

    public void procesarTeclaPresionada(int key) {
        if (finDelJuego) return;
        // ... (Tu código exacto de keyPressed) ...
        if (manejoTurnos.getJugadorActual() == manejoTurnos.getJugador1()) {
            if (key == java.awt.event.KeyEvent.VK_A) moverIzq = true;
            if (key == java.awt.event.KeyEvent.VK_D) moverDer = true;
            if (key == java.awt.event.KeyEvent.VK_S) {
                moverAbajo = true;
                gravityTimer.setDelay(velocidadRapida);
            }
            if (key == java.awt.event.KeyEvent.VK_W) rotar = true;
        } else {
            if (key == java.awt.event.KeyEvent.VK_LEFT) moverIzq = true;
            if (key == java.awt.event.KeyEvent.VK_RIGHT) moverDer = true;
            if (key == java.awt.event.KeyEvent.VK_DOWN) {
                moverAbajo = true;
                gravityTimer.setDelay(velocidadRapida);
            }
            if (key == java.awt.event.KeyEvent.VK_UP) rotar = true;
        }
    }

    public void procesarTeclaLiberada(int key) {
        // ... (Tu código exacto de keyReleased) ...
        if (key == java.awt.event.KeyEvent.VK_A) moverIzq = false;
        if (key == java.awt.event.KeyEvent.VK_D) moverDer = false;
        if (key == java.awt.event.KeyEvent.VK_W) rotar = false;
        if (key == java.awt.event.KeyEvent.VK_S) {
            moverAbajo = false;
            gravityTimer.setDelay(velocidadGravedad);
        }
        if (key == java.awt.event.KeyEvent.VK_LEFT) moverIzq = false;
        if (key == java.awt.event.KeyEvent.VK_RIGHT) moverDer = false;
        if (key == java.awt.event.KeyEvent.VK_UP) rotar = false;
        if (key == java.awt.event.KeyEvent.VK_DOWN) {
            moverAbajo = false;
            gravityTimer.setDelay(velocidadGravedad);
        }
    }

    private void resetearTeclas() {
        moverIzq = false;
        moverDer = false;
        moverAbajo = false;
        rotar = false;
        gravityTimer.setDelay(velocidadGravedad);
    }

    public Jugador calcularGanador() {
        if (manejoTurnos.getJugadorActual() == manejoTurnos.getJugador1()) {
            return manejoTurnos.getJugador2();
        } else {
            return manejoTurnos.getJugador1();
        }
    }

    // --- MÉTODOS "GETTER" PÚBLICOS (Para la Vista) ---

    public Color[][] getTablero() { return tablero; }
    public PiezaPadre getPiezaActual() { return piezaActual; }
    public double getAnguloInclinacion() { return anguloInclinacion; }
    public boolean isFinDelJuego() { return finDelJuego; }
    public String getRazonFinJuego() { return razonFinJuego; }
    public int getSegundosTranscurridos() { return segundosTranscurridos; }
    public Jugador getJugador1() { return manejoTurnos.getJugador1(); }
    public Jugador getJugador2() { return manejoTurnos.getJugador2(); }
    public ManejoTurnos getManejoTurnos() { return manejoTurnos; }


}
