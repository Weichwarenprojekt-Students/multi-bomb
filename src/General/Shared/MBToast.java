package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

public class MBToast {

    /**
     * Make toast with green background
     *
     * @param text of the toast
     */
    public static void success(String... text) {
        new Thread(() -> new Toast(Color.green, text)).start();
    }

    /**
     * Make toast with red background
     *
     * @param text of the toast
     */
    public static void error(String... text) {
        new Thread(() -> new Toast(Color.red, text)).start();
    }

    private static class Toast extends JPanel {

        /**
         * Duration of the toast
         */
        public static final int DURATION = 3000;
        /**
         * The margin of the toast
         */
        public static final int MARGIN = 16;
        /**
         * The padding of the toast
         */
        public static final int PADDING = 8;
        /**
         * The color of the toast
         */
        private final Color color;
        /**
         * The actual toast window
         */
        private final JWindow window = new JWindow();
        /**
         * The measurements of the toast
         */
        private int width, height;

        /**
         * Make the toast
         *
         * @param color of the toast
         * @param text  of the toast
         */
        private Toast(Color color, String... text) {
            this.color = color;
            setLayout(null);

            // Create the labels
            for (String message : text) {
                MBLabel label = new MBLabel(message);
                label.setFontColor(Color.WHITE);
                label.setBold();
                label.alignTextTop();

                // Update the width
                int labelWidth = label.getFontMetrics(label.getFont()).stringWidth(message);
                label.setBounds(PADDING, PADDING + height, labelWidth, MBLabel.NORMAL + PADDING);
                width = Math.max(labelWidth, width);

                // Update height and add the label
                height += label.getHeight();
                add(label);
            }

            // Show the toast
            window.add(this);
            window.setVisible(true);
            window.setAlwaysOnTop(true);
            resizeToast();

            // Follow the frame
            MB.frame.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    resizeToast();
                }

                public void componentMoved(ComponentEvent e) {
                    resizeToast();
                }

                public void componentShown(ComponentEvent e) {
                }

                public void componentHidden(ComponentEvent e) {
                }
            });

            // Show the toast
            try {
                Thread.sleep(DURATION);
                window.setVisible(false);
            } catch (InterruptedException e) {
                window.setVisible(false);
            }
        }

        /**
         * Resize the toast
         */
        private void resizeToast() {
            setBounds(0, 0, width + 2 * PADDING, height + PADDING);
            int decorationHeight = MB.frame.getHeight() - MB.activePanel.getHeight();
            window.setBounds(
                    MB.frame.getX() + MB.frame.getWidth() - width - MARGIN - 2 * PADDING,
                    MB.frame.getY() + MARGIN + decorationHeight,
                    getWidth(),
                    getHeight()
            );
            window.setShape(new RoundRectangle2D.Double(0, 0, window.getWidth(), window.getHeight(), 6, 6));
        }

        /**
         * Paint the toast
         *
         * @param g Graphics
         */
        public void paintComponent(Graphics g) {
            super.paintComponents(g);
            MB.settings.enableAntiAliasing(g);
            // Set the color and calculate the diameter
            g.setColor(color);
            g.fillRect(
                    0,
                    0,
                    window.getWidth(),
                    window.getHeight()
            );
        }
    }
}
