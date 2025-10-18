package Bloques;

import java.awt.*;

public class PiezaO extends PiezaPadre{
    public PiezaO(){
        this.forma = new int[][]{
                {1, 1},
                {1, 1}
        };
        this.color = new Color(193, 163, 48);
        this.peso = 4;
    }
}
