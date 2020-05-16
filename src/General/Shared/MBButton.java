package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MBButton extends JButton {

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

        // The hover effect
        if (hasFocus()) {
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        // The border and the text
        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.setFont(new Font("Calibri", Font.PLAIN, MBLabel.NORMAL));
        g.drawString(
                text,
                getWidth() / 2 - g.getFontMetrics().stringWidth(text) / 2,
                getHeight() / 2 + 4
        );
    }
}
