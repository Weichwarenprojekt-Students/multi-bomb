package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MBDialog extends JPanel {

    /**
     * The radius of the corners
     */
    private static final int CORNER_RADIUS = 3;
    /**
     * The content of the dialog
     */
    private final JPanel content;

    /**
     * Constructor
     */
    public MBDialog(JPanel content) {
        this.content = content;

        // Modify the panel
        setLayout(null);
        setVisible(false);
        setOpaque(false);
        add(content);

        // Add resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerContent();
            }
        });
        content.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerContent();
            }
        });

        // Close the dialog on click
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                // Check whether the mouse is outside the dialog
                boolean outsideX = e.getX() < content.getX() || e.getX() > content.getX() + content.getWidth();
                boolean outsideY = e.getY() < content.getY() || e.getY() > content.getY() + content.getHeight();
                // Close the dialog
                if (outsideX || outsideY) {
                    close();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    /**
     * Center the content panel
     */
    private void centerContent() {
        content.setBounds(
                getWidth() / 2 - content.getWidth() / 2,
                getHeight() / 2 - content.getHeight() / 2,
                content.getWidth(),
                content.getHeight()
        );
    }

    /**
     * Open the dialog
     */
    public void open() {
        setVisible(true);
    }

    /**
     * Close the dialog
     */
    public void close() {
        setVisible(false);
    }

    /**
     * Paint the dialog
     *
     * @param g graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        MB.settings.enableAntiAliasing(g);

        // Draw the background
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        // Set the color and calculate the diameter
        g.setColor(Color.white);
        int cornerDiameter = 2 * CORNER_RADIUS;
        // Upper Left corner
        g.fillOval(
                content.getX() - CORNER_RADIUS,
                content.getY() - CORNER_RADIUS,
                cornerDiameter,
                cornerDiameter
        );
        // Upper right corner
        g.fillOval(
                content.getX() + content.getWidth() - CORNER_RADIUS,
                content.getY() - CORNER_RADIUS,
                cornerDiameter,
                cornerDiameter
        );
        // Lower left corner
        g.fillOval(
                content.getX() - CORNER_RADIUS,
                content.getY() + content.getHeight() - CORNER_RADIUS,
                cornerDiameter,
                cornerDiameter
        );
        // Lower right corner
        g.fillOval(
                content.getX() + content.getWidth() - CORNER_RADIUS,
                content.getY() + content.getHeight() - CORNER_RADIUS,
                cornerDiameter,
                cornerDiameter
        );

        // Fill the dialog
        g.fillRect(
                content.getX(),
                content.getY() - CORNER_RADIUS,
                content.getWidth(),
                content.getHeight() + cornerDiameter
        );
        g.fillRect(
                content.getX() - CORNER_RADIUS,
                content.getY(),
                content.getWidth() + cornerDiameter,
                content.getHeight()
        );
    }
}
