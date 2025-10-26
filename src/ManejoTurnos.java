public class ManejoTurnos {
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador jugadorActual;

    public  ManejoTurnos(Jugador j1, Jugador j2) {
        this.jugador1 = j1;
        this.jugador2 = j2;
        this.jugadorActual = j1;
    }

    public void cambiarTurno(){
        if(jugadorActual == jugador1){
            jugadorActual = jugador2;
        }else {
            jugadorActual = jugador1;
        }
    }

    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }
}
