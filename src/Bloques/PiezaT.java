package Bloques;
import java.awt.Color;
public class PiezaT extends PiezaPadre{
    public PiezaT(){
        this.forma = new int [][]{
                {0, 1, 0},
                {1, 1, 1}
        };
        this.color = new Color(174, 16, 200);
        this.peso = 3;
    }
}
