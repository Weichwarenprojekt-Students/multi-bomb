package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;

public class MBSpinner extends JPanel {

    /**
     * Speed of the spinner
     */
    private static final int SPEED = 2;
    /**
     * The progress of spinning
     */
    private int progress = 0;
    /**
     * The additional delta angle
     */
    private int deltaAngle = 0;

    /**
     * Constructor
     */
    public MBSpinner() {
        setLayout(null);
        setOpaque(false);
        new Thread(this::spin).start();
    }

    /**
     * Start the animation if visible
     *
     * @param visible true if component shall be visible
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            new Thread(this::spin).start();
        }
    }

    /**
     * Spin the spinner
     */
    private void spin() {
        while (isVisible()) {
            // Update the progress
            progress += deltaAngle == 0 ? SPEED : -SPEED;

            // Check if progress reached a limit
            if (progress > 180) {
                progress = 0;
                deltaAngle = 180;
            } else if (progress < -180) {
                progress = 0;
                deltaAngle = 0;
            }

            // Repaint and wait
            revalidate();
            repaint();
            sleep();
        }
    }

    /**
     * Sleep until animation continues
     */
    private void sleep() {
        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        MB.settings.enableAntiAliasing(g);
        g.setColor(Color.WHITE);
        g.fillArc(0, 0, getWidth(), getHeight(), progress + deltaAngle, -2 * progress);
    }
}
