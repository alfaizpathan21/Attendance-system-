import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private Color startColor = new Color(99, 102, 241);
    private Color endColor = new Color(129, 140, 248);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(
                0, 0, startColor,
                getWidth(), getHeight(), endColor
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}