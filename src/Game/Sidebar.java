package Game;

import General.MB;
import General.Shared.MBImageButton;
import General.Shared.MBLabel;
import General.Shared.MBPanel;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends MBPanel {

    /**
     * Constructor
     */
    public Sidebar() {
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBLabel title = new MBLabel("Game", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, 50, 200, 40));
    }

    @Override
    public void afterVisible() {
        // The pause button
        MBImageButton pause = new MBImageButton("General/pause.png");
        pause.addActionListener(() -> Game.overlay.setVisible(!Game.overlay.isVisible()));
        addComponent(pause, () -> pause.setBounds(8, 8, 24, 24));
    }

    /**
     * Draw the background
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        MB.settings.enableAntiAliasing(g);
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
