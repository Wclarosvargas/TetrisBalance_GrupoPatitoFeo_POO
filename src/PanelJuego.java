import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.awt.Graphics2D;

public class PanelJuego extends JPanel implements ActionListener {
    private static final int anchoPanel = 300;
    private static final int altoPanel = 600;

    private static final int tamanioBloque = 20;
    private static final int anchoTablero = anchoPanel / tamanioBloque; // Resultado: 10
    private static final int altoTablero = altoPanel / tamanioBloque;

    //plataforma
    private double anguloPlataforma = 0.0;
    private final double anguloMaximo = 6.0;
    private final int anchoPlataforma = 200;
    private final int altoPlataforma = 20;
    private final int filaSuperficiePlataforma = (altoPanel-60 - altoPlataforma) / tamanioBloque; //calcula el nro de filas arriba de la plataforma
    private final int columnaInicioPlataforma = ((anchoPanel / 2) - (anchoPlataforma / 2)) / tamanioBloque;
    private final int columnaFinPlataforma = ((anchoPanel / 2)+ (anchoPlataforma/2))/tamanioBloque;

    //Piezas
    private PiezaPadre piezaActual;
    private Random aleatorio = new Random();
    private Color[][] tablero;

    //Game Over
    private Timer timer;



    //Metodo para agregar bordes negros a las piezas
    private void bordeNegro(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x, y, tamanioBloque, tamanioBloque);

        g.setColor(Color.BLACK);
        g.drawRect(x,y,tamanioBloque,tamanioBloque);
    }

    private void generarNuevaPieza(){
        int indice = aleatorio.nextInt(7);

        switch (indice){
            case 0:
                piezaActual = new PiezaI();
                break;
            case 1:
                piezaActual = new PiezaL();
                break;
            case 2:
                piezaActual = new PiezaJ();
                break;
            case 3:
                piezaActual = new PiezaT();
                break;
            case 4:
                piezaActual = new PiezaO();
                break;
            case 5:
                piezaActual = new PiezaS();
                break;
            case 6:
                piezaActual = new PiezaZ();
                break;
        }
        //Posición en el centro
        int xInicial = (anchoTablero - piezaActual.getAncho()) / 2;
        piezaActual.setX(xInicial);
        piezaActual.setY(0);

        if(!esMovimientoValido(piezaActual,piezaActual.getX(),piezaActual.getY())){
            finJuego("¡Alcanzaste la cima!");
        }

    }

    private boolean esMovimientoValido(PiezaPadre pieza, int nuevaX, int nuevaY) {
        int[][] forma = pieza.getForma();

        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    int columnaGrilla = nuevaX + columna;
                    int filaGrilla = nuevaY + fila;

                    //Colisión con los bordes del panel
                    if (columnaGrilla < 0 || columnaGrilla >= anchoTablero || filaGrilla >= altoTablero) {
                        return false;
                    }

                    // Colisión con otras piezas ya fijadas
                    if (filaGrilla >= 0 && tablero[filaGrilla][columnaGrilla] != null) {
                        return false;
                    }


                    boolean estaSobrePlataforma = columnaGrilla >= columnaInicioPlataforma && columnaGrilla < columnaFinPlataforma;



                    if (filaGrilla == filaSuperficiePlataforma && estaSobrePlataforma) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Metodo pieza fuera de la plataforma
    private boolean esPiezaFueraPlataforma(){
        int[][] forma = piezaActual.getForma();

        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                boolean esUnBloque = forma[fila][columna] == 1;

                if (esUnBloque) {
                    int columnaGrilla = piezaActual.getX() + columna;

                    boolean estaSobrePlataforma = columnaGrilla >= columnaInicioPlataforma && columnaGrilla < columnaFinPlataforma;

                    if (estaSobrePlataforma) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    //GameOver
    private void finJuego(String mensaje){
        this.timer.stop(); //encargado de detener las caidas de las piezas
        piezaActual = null; //evita que siga dibujando piezas
        repaint();
        JOptionPane.showMessageDialog(this,"Fin del juego");
    }

    //Metodo encargado del equilibrio de la plataforma
    private void equilibrioPlataforma(){
        double desequilibrioTotal = 0;


        double centroPlataforma = (anchoTablero -1) / 2.0;

        //recorre toda la grilla para medir el peso de cada bloque
        for (int fila = 0; fila < tablero.length; fila++) {
            for (int columna = 0; columna < tablero[fila].length; columna++) {
                if (tablero[fila][columna] != null){
                    double distanciaDelCentro = columna -centroPlataforma;
                    desequilibrioTotal += distanciaDelCentro;
                  }
            }
        }

        //Convierte el desequilibrio en un angulo
        double nuevoAngulo = desequilibrioTotal * 0.5;

        this.anguloPlataforma = Math.max(-45.0, Math.min(45.0, nuevoAngulo));
    }

    //Constructor
    public PanelJuego() {
        setPreferredSize(new Dimension(anchoPanel, altoPanel));
        setBackground(Color.BLACK);
        setFocusable(true);

        tablero = new Color[altoTablero][anchoTablero];
        limpiarTablero();

        generarNuevaPieza();

        //Tiempo del bloque
        this.timer = new Timer(900,this);
        this.timer.start();

        addKeyListener(new ControlesJuego());
    }

    private void limpiarTablero() {
        for (int i = 0; i < altoTablero; i++) {
            for (int j = 0; j < anchoTablero; j++) {
                tablero[i][j] = null;
            }
        }
    }

    //metodo fijar la pieza
    private void fijarPiezaEnMemoria(){
        int[][] forma = piezaActual.getForma();
        for (int fila = 0; fila < forma.length; fila++) {
            for (int columna = 0; columna < forma[fila].length; columna++) {
                if (forma[fila][columna] == 1) {
                    int x = piezaActual.getX() + columna;
                    int y = piezaActual.getY() + fila;
                    if (y >= 0){
                        tablero[y][x] = piezaActual.getColor();
                    }
                }
            }
        }
    }

    private class ControlesJuego extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (piezaActual == null) return;
            int keyCode = e.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    if(esMovimientoValido(piezaActual, piezaActual.getX()-1,piezaActual.getY() )) {
                        piezaActual.moverIzquierda();
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(esMovimientoValido(piezaActual, piezaActual.getX()+1,piezaActual.getY() )) {
                        piezaActual.moverDerecha();
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(esMovimientoValido(piezaActual, piezaActual.getX(),piezaActual.getY()+1)) {
                        piezaActual.moverAbajo();
                    }else{
                        fijarPiezaEnMemoria();
                        if (esPiezaFueraPlataforma()){
                            finJuego("La pieza cayo fuera de la plataforma");
                        }else{
                        equilibrioPlataforma();
                        generarNuevaPieza();
                        }
                    }
                    break;
                case KeyEvent.VK_UP:
                    piezaActual.rotar();
                    break;
            }
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        //Cálculo del punto central
        int privoteX = anchoPanel / 2;
        int privoteY = altoPanel - 60;

        g.setColor(Color.WHITE);
        int[] xPuntos = {
                privoteX - 40, privoteX,privoteX +40
        };
        int[] yPuntos = {
                privoteY + 40, privoteY,privoteY +40
        };
        g.fillPolygon(xPuntos, yPuntos, 3);


        AffineTransform oldTransform = g2d.getTransform();

        g2d.translate(privoteX, privoteY);
        g2d.rotate(Math.toRadians(anguloPlataforma)); //rotación según el ángulo

        g.setColor(Color.GRAY);
        g2d.fillRect(-anchoPlataforma /2, -altoPlataforma, anchoPlataforma, altoPlataforma);

        //Dibuja las piezas fijas
        for (int fila = 0; fila < tablero.length; fila++) {
            for (int columna = 0; columna < tablero[fila].length; columna++) {
                if (tablero[fila][columna] != null) {
                    int xAbsoluto = columna *tamanioBloque;
                    int yAbsoluto = fila *tamanioBloque;

                    int xRelativo = xAbsoluto - privoteX;
                    int yRelativo = yAbsoluto - privoteY;
                    bordeNegro(g,xRelativo,yRelativo, tablero[fila][columna]);
                }
            }
        }
        //Termina las piezas rotadas
        g2d.setTransform(oldTransform);

        if (piezaActual != null) {
            int[][] forma = piezaActual.getForma();
            for (int fila = 0; fila < forma.length; fila++) {
                for (int columna = 0; columna < forma[fila].length; columna++) {
                    if (forma[fila][columna] == 1) {
                        int x = (piezaActual.getX()+ columna)*tamanioBloque;
                        int y = (piezaActual.getY() + fila)*tamanioBloque;
                        bordeNegro(g,x,y,piezaActual.getColor());
                    }
                }
            }
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (piezaActual == null) return;

        //Permite que la pieza caiga, si se lo permite
        if(esMovimientoValido(piezaActual, piezaActual.getX(), piezaActual.getY()+1)){
            piezaActual.moverAbajo();
        }else{
            fijarPiezaEnMemoria();
            if (esPiezaFueraPlataforma()){
                finJuego("La pieza cayo fuera de la plataforma");
            }else {
                equilibrioPlataforma();
                if (Math.abs(anguloPlataforma )>anguloMaximo){
                    finJuego("¡La plataforma se inclino fuera de los limites!");
                }else {
                    generarNuevaPieza();
                }

            }
        }

        repaint();

    }


}
