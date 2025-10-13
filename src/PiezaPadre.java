import java.awt.Color;

public abstract class PiezaPadre {
    private int [][] forma;
    private Color color;
    private int x,y; //define la posición de la pieza

    //Constructor
    public PiezaPadre(int [][] forma, Color color, int x, int y) {
        this.forma = forma;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public PiezaPadre(int[][] forma, Color color) {
        this.forma = forma;
        this.color = color;
        this.x = 0;
        this.y = 0;
    }


    //Metodo Getters y Setters
    public int[][] getForma() {
        return forma;
    }
    public Color getColor() {
        return color;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }

    //Métodos para mover la pieza
    public void moverAbajo(){
        y++;
    }
    public void moverIzquierda(){
        x--;
    }
    public void moverDerecha(){
        x++;
    }

    public int getAlto(){
        return forma.length;
    }
    public int getAncho(){
        return forma[0].length;
    }

    public void rotar() {
        int[][] nuevaForma = new int[forma[0].length][forma.length];
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[0].length; j++) {
                nuevaForma[j][forma.length - 1 - i] = forma[i][j];
            }
        }
        this.forma = nuevaForma;
    }


}
