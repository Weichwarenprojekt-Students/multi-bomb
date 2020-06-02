package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MBScrollView extends JPanel {

    /**
     * The colors of the scrollbar
     */
    public static final Color BACKGROUND_COLOR = new Color(0xA9A9A9), COLOR = new Color(0x696969);
    /**
     * Size of the scrollbar
     */
    private static final int SIZE = 8;
    /**
     * The margin of the scrollbar
     */
    private static final int MARGIN = 2;
    /**
     * The speed of scrolling
     */
    private static final int SPEED = 20;
    /**
     * True if the component is scrollable
     */
    private boolean scrollable = false;
    /**
     * True if the mouse was pressed on the bar
     */
    private boolean dragged = false;
    /**
     * The distance of the top of the bar to the mouse position
     */
    private int deltaY = 0;
    /**
     * The measurements for the scrollbar
     */
    private int barY, barHeight;
    /**
     * The content of the scroll view
     */
    JPanel content;

    /**
     * Constructor
     *
     * @param content for the scroll view
     */
    public MBScrollView(JPanel content) {
        this.content = content;
        setLayout(null);
        setOpaque(false);
        setupScrollBehavior();
        setupComponent();
    }

    /**
     * Setup the scroll behavior for the mousewheel and the dragging
     */
    private void setupScrollBehavior() {
        // Make the component scrollable
        addMouseWheelListener(e -> scroll(content.getY() - e.getWheelRotation() * SPEED));

        // Make the scrollbar draggable
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseScroll(e.getY());
            }
            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        // Make the scrollbar clickable
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                // Renew the mouse state
                dragged = e.getX() > getWidth() - SIZE - 2 * MARGIN;
                deltaY = e.getY() - barY;

                // Scroll the bar
                mouseScroll(e.getY());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                dragged = false;
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
     * Scroll the bar with the mouse
     *
     * @param y position of the mouse
     */
    private void mouseScroll(int y) {
        if (dragged) {
            int newY = (int) -((float) (y - deltaY) / getHeight() * content.getHeight());
            scroll(newY);
        }
    }


    /**
     * Scroll the bar to a given position
     *
     * @param newY the new y position of the scrollbar
     */
    private void scroll(int newY) {
        // Check if the component is scrollable
        if (!scrollable) {
            return;
        }

        // Check if scrollbar reached the end
        if (newY >= 0) {
            newY = 0;
        } else if (-newY + getHeight() > content.getHeight()) {
            newY = getHeight() - content.getHeight();
        }

        // Update position and repaint
        this.content.setLocation(0, newY);
        barY = (int) ((float) getHeight() * -content.getY() / content.getHeight());
        barHeight = (int) ((float) getHeight() * getHeight() / content.getHeight());
        repaint();
    }

    /**
     * Add the content
     */
    private void setupComponent() {
        add(content);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onResize();
            }
        });
        onResize();
    }

    /**
     * Handle resizing
     */
    private void onResize() {
        // Check if the scroll view needs to be scrollable
        scrollable = getHeight() < content.getHeight();

        // Resize the content
        content.setBounds(
                0,
                content.getY(),
                scrollable ? getWidth() - SIZE - 2 * MARGIN : getWidth(),
                content.getHeight()
        );

        // Update the scroll position (to make it scroll up if height increases when the bar is at the end)
        scroll(content.getY());
    }

    /**
     * Draw a scrollbar
     *
     * @param g graphics
     * @param y position of the scrollbar
     * @param height of the scrollbar
     */
    private void drawBar(Graphics g, int y, int height) {
        // Respect the margin
        int x = getWidth() - SIZE - MARGIN;
        y += MARGIN;
        height -= 2 * MARGIN;

        // Draw the bar
        g.fillOval(x, y, SIZE, SIZE);
        g.fillRect(x, y + SIZE / 2, SIZE, height - SIZE);
        g.fillOval(x, y + height - SIZE, SIZE, SIZE);
    }

    /**
     * Paint the scrollbar
     *
     * @param g graphics
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        MB.settings.enableAntiAliasing(g);
        if (scrollable) {
            g.setColor(BACKGROUND_COLOR);
            drawBar(g, 0, getHeight());
            g.setColor(COLOR);
            drawBar(g, barY, barHeight);
        }
    }
}
