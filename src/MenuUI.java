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
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/resources/menu_bg.jpg"));
        Image background = bgIcon.getImage().getScaledInstance(400, 600, Image.SCALE_SMOOTH);

        // Panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // Centra el menu

        // Panel de menu
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        menuPanel.setOpaque(false);
        menuPanel.setPreferredSize(new Dimension(250, 300));

        JLabel title = new JLabel("", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.RED);

        JButton startBtn = makeButton("Iniciar juego");
        JButton helpBtn = makeButton("Controles");
        JButton exitBtn = makeButton("Salir");

        menuPanel.add(title);
        menuPanel.add(startBtn);
        menuPanel.add(helpBtn);
        menuPanel.add(exitBtn);

        // Agrega el menu al centro del fondo
        backgroundPanel.add(menuPanel);
        setContentPane(backgroundPanel);

        // Botones
        startBtn.addActionListener(e -> {
            // Cerrar el menú
            dispose();
            // Iniciar el juego
            SwingUtilities.invokeLater(() -> {
                //new Tetris();
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

        // Efecto de botones
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuUI::new);
    }
}