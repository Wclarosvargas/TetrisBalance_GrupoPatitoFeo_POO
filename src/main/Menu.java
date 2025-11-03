package main;

import javax.swing.*;
import java.awt.*;

/**
Clase base para todos los menús del juego.
Proporciona: configuración de ventana, fondo, y estilo uniforme de botones.
 */

public abstract class Menu extends JFrame {

    private Image backgroundImage;

    /*
    -Constructor general para los menús.s
    @param titulo título de la ventana
    @param anchoa ancho en píxeles
    @param alto alto en píxeles
    @param rutaFondo ruta de la imagen de fondo (por ejemplo "/resources/menu_bg.jpeg")
    */

    public Menu(String titulo, int ancho, int alto, String rutaFondo) {
        setTitle(titulo);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        setResizable(false);

        // Cargar fondo si se proporciona ruta
        if (rutaFondo != null && !rutaFondo.isEmpty()) {
            ImageIcon bgIcon = new ImageIcon(getClass().getResource(rutaFondo));
            backgroundImage = bgIcon.getImage();
        }

        // Crear panel con fondo
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // centrado por defecto
        setContentPane(backgroundPanel);

        // Delega en la subclase la creación del contenido específico
        inicializarComponentes(backgroundPanel);
    }

    
    //Método que deben implementar las subclases para agregar sus botones, paneles, etc.

    protected abstract void inicializarComponentes(JPanel fondo);

    //Crea un botón con estilo uniforme (fuente, color, bordes, efecto hover, etc.)

    protected JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(30, 30, 30));
            }
        });

        return btn;
    }
}

