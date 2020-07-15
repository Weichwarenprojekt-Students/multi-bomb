package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MBBackground extends JLabel {

    /**
     * The corner radius
     */
    public static final int CORNER_RADIUS = 6;
    /**
     * The color of the background
     */
    private final Color color;

    /**
     * Constructor
     *
     * @param color of the background
     */
    public MBBackground(Color color) {
        this.color = color;
    }

    /**
     * Constructor
     *
     * @param parent of the background
     * @param color  of the background
     * @param follow true if the background shall follow the parent
     */
    public MBBackground(Component parent, Color color, boolean follow) {
        this.color = color;

        // Fill the parent
        parent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize(parent, follow);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                resize(parent, follow);
            }
        });
        resize(parent, follow);
    }

    /**
     * Resize the background
     *
     * @param parent of the background
     * @param follow true if the background should follow the parent
     */
    private void resize(Component parent, boolean follow) {
        if (follow) {
            setBounds(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight());
        } else {
            setBounds(0, 0, parent.getWidth(), parent.getHeight());
        }
    }

    /**
     * Paint the toast
     *
     * @param g graphics
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        MB.settings.enableAntiAliasing(g);

        // Set the color and draw the rounded rectangle
        g.setColor(color);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
    }
}
