import Bloques.PiezaPadre;

import java.awt.*;


public class Jugador {
    private String nombre;
    private int puntaje;
    private PiezaPadre piezaActual;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.puntaje = 0;
    }

    public String getNombre() {
        return nombre;
    }
    public int getPuntaje() {
        return puntaje;
    }
    public void sumarPuntos(int puntos) {
        this.puntaje += puntos;
    }
    public PiezaPadre getPiezaActual() {
        return piezaActual;
    }

    public void setPiezaActual(PiezaPadre piezaActual) {
        this.piezaActual = piezaActual;
    }
}
