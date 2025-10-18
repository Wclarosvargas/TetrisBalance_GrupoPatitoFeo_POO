package Bloques;
import java.awt.*;

public class PiezaS extends PiezaPadre{
    public PiezaS(){
        this.forma = new int[][]{
                {0, 1, 1},
                {1, 1, 0}
        };
        this.color = new Color(33, 158, 25);
        this.peso = 3;
    }
}
