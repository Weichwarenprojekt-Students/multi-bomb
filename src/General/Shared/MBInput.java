package General.Shared;

import javax.swing.*;
import java.awt.*;

public class MBInput extends JTextField {

    /**
     * Padding of the cursor
     */
    private static final int PADDING = 8;

    /**
     * Constructor
     */
    public MBInput() {
        setFont(new Font(MBLabel.FONT_NAME, Font.PLAIN, MBLabel.NORMAL));
        setBackground(MBScrollView.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
    }

    /**
     * Paint the border
     *
     * @param g the graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.drawRect(0, getHeight() - 1, getWidth(), 0);
        g.drawRect(0, getHeight() - 10, 0, 10);
        g.drawRect(getWidth() - 1, getHeight() - 10, 0, 10);
    }
}
