package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MBButton extends JButton {

    /**
     * The transparency of the background
     */
    public static final float TRANSPARENCY = 0.2f;
    /**
     * The color of non-highlighted text
     */
    public static final Color GREY = new Color(230, 230, 230);
    /**
     * The background color
     */
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, TRANSPARENCY);
    /**
     * The font for the buttons
     */
    private static final Font font = new Font(MBLabel.FONT_NAME, Font.BOLD, MBLabel.NORMAL);
    /**
     * True if the button is enabled
     */
    public boolean enabled = true;
    /**
     * The Button text
     */
    private String text;

    /**
     * A custom button
     *
     * @param text of the button
     */
    public MBButton(String text) {
        this.text = text;
        setBorderPainted(false);
        setBackground(null);
        setOpaque(false);

        // Check if mouse is hovering
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                requestFocus();
                repaint();
            }
        });

        // Enable enter for button clicking
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doClick();
                }
            }
        });

        // Disable the space key
        getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
    }

    /**
     * Set the button text
     *
     * @param text of the button
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Paint the button
     *
     * @param g graphics
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        MB.settings.enableAntiAliasing(g);

        // The border and the text
        g.setColor(BACKGROUND_COLOR);
        g.fillRoundRect(
                0,
                0,
                getWidth() - 1,
                getHeight() - 1,
                MBBackground.CORNER_RADIUS,
                MBBackground.CORNER_RADIUS
        );

        // The hover effect
        if (hasFocus() && enabled) {
            g.setColor(Color.white);
        } else {
            g.setColor(GREY);
        }
        g.setFont(font);
        g.drawString(
                text,
                getWidth() / 2 - g.getFontMetrics().stringWidth(text) / 2,
                getHeight() / 2 + 4
        );
    }
}
