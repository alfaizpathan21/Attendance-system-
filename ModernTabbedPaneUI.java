import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class ModernTabbedPaneUI extends BasicTabbedPaneUI {
    private Color selectedColor = new Color(99, 102, 241);
    private Color unselectedColor = new Color(203, 213, 225);

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabAreaInsets.right = 20;
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement,
                                      int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected) {
            g2d.setColor(selectedColor);
        } else {
            g2d.setColor(unselectedColor);
        }

        g2d.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 15, 15);
        g2d.dispose();
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement,
                                  int tabIndex, int x, int y, int w, int h, boolean isSelected) {}

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}
}