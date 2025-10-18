package Bloques;
import java.awt.Color;

public class PiezaZ extends PiezaPadre {
    public PiezaZ() {
        this.forma = new int[][]{
                {1, 1, 0},
                {0, 1, 1}
        };
        this.color = new Color(143, 27, 27);
        this.peso = 3;
    }
}
