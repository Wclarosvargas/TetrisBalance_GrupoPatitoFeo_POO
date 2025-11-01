package motorJuego;

import Bloques.PiezaPadre;


public class Jugador {
    private String nombre;
    private PiezaPadre piezaActual;


    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
    public PiezaPadre getPiezaActual() {
        return piezaActual;
    }

    public void setPiezaActual(PiezaPadre piezaActual) {
        this.piezaActual = piezaActual;
    }

}
