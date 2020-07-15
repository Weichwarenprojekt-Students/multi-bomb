package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MBImageButton extends JLabel {

    /**
     * True if the button shall be enabled
     */
    public boolean enabled = true;
    /**
     * The Button image
     */
    private MBImage image;

    /**
     * A custom button
     *
     * @param path of the image
     */
    public MBImageButton(String path) {
        this.image = new MBImage(path, true, null);
    }

    /**
     * Add click action
     *
     * @param listener to be executed if image was clicked
     */
    public void addActionListener(ActionListener listener) {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (enabled) {
                    listener.onPressed();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                MB.frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                MB.frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    /**
     * Set the button image
     *
     * @param path of the image
     */
    public void setImage(String path) {
        this.image = new MBImage(path, true, null);
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
        g.drawImage(
                image.image,
                0,
                0,
                getWidth(),
                getHeight(),
                null
        );
    }

    /**
     * Interface for click events
     */
    public interface ActionListener {
        void onPressed();
    }
}
