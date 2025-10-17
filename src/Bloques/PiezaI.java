package Bloques;
import java.awt.Color;

public class PiezaI extends PiezaPadre{
    public PiezaI(){
        this.forma = new int[][]{
                {1,1,1,1}
        };
        this.color = new Color(0,255,255);
        this.peso = 1;
    }
}
