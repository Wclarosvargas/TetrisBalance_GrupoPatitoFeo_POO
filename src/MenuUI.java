import javax.swing.*;
import java.awt.*;

public class MenuUI extends JFrame {

    public MenuUI() {
        setTitle("Tetris Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Imagen de fondo
        ImageIcon bgIcon = new ImageIcon("TetrisBalance_GrupoPatitoFeo_POO\\src\\..."); //falta imagen
        Image background = bgIcon.getImage().getScaledInstance(400, 600, Image.SCALE_SMOOTH);

        /*
        // Custom panel that paints the background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // center the menu nicely

        */

        // Menu panel with GridLayout
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        menuPanel.setOpaque(false); // transparent so background is visible
        menuPanel.setPreferredSize(new Dimension(250, 300));

        JLabel title = new JLabel("TETRIS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.RED);

        JButton startBtn = makeButton("Iniciar juego");
        JButton helpBtn = makeButton("Controles");
        JButton exitBtn = makeButton("Salir");

        menuPanel.add(title);
        menuPanel.add(startBtn);
        menuPanel.add(helpBtn);
        menuPanel.add(exitBtn);

        // Add menu to center of background
        backgroundPanel.add(menuPanel);
        setContentPane(backgroundPanel);

        // Button actions
        startBtn.addActionListener(e -> {
            // Cerrar el menú
            dispose();
            // Iniciar el juego
            SwingUtilities.invokeLater(() -> {
                new Tetris();
                new VentanaPrincipal();
            });
        });
        
        helpBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Controles:\n← → ↑ ↓ Mover\n (Espacio) Rotar Bloque "));
        
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }
    
    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        // Add hover effect manually
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60)); // lighter gray when hovered
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(30, 30, 30)); // back to normal
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuUI::new);
    }
}