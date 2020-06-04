package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MBSlider extends JLabel {

    /**
     * The percentage of the slider
     */
    private int percentage;
    /**
     * The event that is triggered when sliding
     */
    private final SlideEvent event;

    /**
     * Constructor
     */
    public MBSlider(int percentage, SlideEvent event) {
        this.event = event;
        this.percentage = percentage;

        // Add listener
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                slide(e.getX());
            }
            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                slide(e.getX());
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
     * Slide the slider
     *
     * @param x position of the mouse
     */
    private void slide(int x) {
        // Check if slide is out of bounds
        if (x <= 0) {
            percentage = 0;
        } else if (x >= getWidth()) {
            percentage = 100;
        } else {
            percentage = (int) ((float)x / getWidth() * 100);
        }

        // Fire the event
        this.event.onSlide(percentage);

        // Repaint
        repaint();
        revalidate();
    }

    /**
     * Paint the slider
     *
     * @param g graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        MB.settings.enableAntiAliasing(g);

        // Draw the slider
        g.setColor(MBScrollView.BACKGROUND_COLOR);
        g.fillRect(0, 0, (int) ((float) percentage / 100 * getWidth()), getHeight());
        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    /**
     * Interface that triggers if the slider was moved
     */
    public interface SlideEvent {
        void onSlide(int percentage);
    }
}
