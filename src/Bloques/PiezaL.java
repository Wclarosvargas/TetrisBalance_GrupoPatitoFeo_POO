package Bloques;

import javax.swing.*;
import java.awt.*;

public class PiezaL extends PiezaPadre {
    public PiezaL() {
        this.forma = new int[][]{
                {1, 0, 0},
                {1, 1, 1}
        };
        this.color = new Color(163, 63, 12);
        this.peso = 2;
    }
}
