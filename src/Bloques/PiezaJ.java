package Bloques;
import java.awt.Color;

public class PiezaJ extends PiezaPadre{
    public PiezaJ() {
        this.forma = new int[][]{
                {0, 0, 1},
                {1, 1, 1}
        };
        this.color = new Color(17, 52, 133);
        this.peso = 2;
    }
}
