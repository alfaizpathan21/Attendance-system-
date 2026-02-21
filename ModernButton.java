import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JButton {
    private Color backgroundColor = new Color(99, 102, 241);
    private Color hoverColor = new Color(129, 140, 248);

    public ModernButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backgroundColor = hoverColor;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                backgroundColor = new Color(99, 102, 241);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
        super.paintComponent(g2);
        g2.dispose();
    }
}