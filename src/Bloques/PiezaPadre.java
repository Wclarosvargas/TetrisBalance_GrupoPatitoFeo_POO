package Bloques;

import java.awt.Color;


public abstract class PiezaPadre {
    //Atributos
    protected int [][] forma;
    protected Color color;
    protected int peso;
    public int x;  //Posición en el tablero
    public int y; //Posición en el tablero


    //----------------------- Metodos ------------------------------

    public int[][] getForma() {
        return forma;
    }
    public Color getColor() {
        return color;
    }
    public int getPeso() {
        return peso;
    }
    public void moverIzq(){
        x--;
    }
    public void moverDer(){
        x++;
    }
    public void moverAbajo(){
        y++;
    }

    //Metodo rotación de pieza
    public void rotar(){
        int[][] piezaRotada = new int[forma[0].length][forma.length];
        for(int i = 0; i < forma.length; i++){
            for(int j = 0; j < forma[0].length; j++){
                piezaRotada[j][forma.length -1 -i]= forma[i][j];
            }
        }
        this.forma = piezaRotada;
    }

    public int getAncho(){
        return forma[0].length;
    }


}
