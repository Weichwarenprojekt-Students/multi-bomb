package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;

public class MBTitle extends JPanel {

    /**
     * The padding of the text
     */
    private static final int PADDING = 8;

    /**
     * Constructor
     */
    public MBTitle(String... text) {
        setLayout(null);
        setVisible(true);
        setOpaque(false);

        // Create the labels
        int width = 0, height = 0;
        for (String message : text) {
            MBLabel label = new MBLabel(MBLabel.TITLE, message);
            label.alignTextTop();

            // Update the width
            int labelWidth = label.getFontMetrics(label.getFont()).stringWidth(message);
            label.setBounds(PADDING, PADDING + height, labelWidth, MBLabel.TITLE + PADDING);
            width = Math.max(labelWidth, width);

            // Update height and add the label
            height += label.getHeight();
            add(label);
        }
        setSize(width + 2 * PADDING, height + PADDING);
    }

    /**
     * Paint a round rectangle around the title
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        MB.settings.enableAntiAliasing(g);
        g.setColor(Color.white);
        g.drawRoundRect(
                0,
                0,
                getWidth() - 1,
                getHeight() - 1,
                MBBackground.CORNER_RADIUS,
                MBBackground.CORNER_RADIUS
        );
    }
}
