package Game;

import General.MB;
import General.Shared.MBButton;
import General.Shared.MBLabel;
import General.Shared.MBPanel;
import Server.LobbyView;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends MBPanel {
    @Override
    public void beforeVisible() {

        // The title
        MBLabel title = new MBLabel("Game", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, 50, 200, 40));

        // The button for opening a lobby overview
        MBButton back = new MBButton("Back");
        back.addActionListener(e -> MB.show(new LobbyView()));
        addComponent(back, () -> back.setBounds(getWidth() / 2 - 70, 100, 140, 40));

        // Add the buttons to a group
        addButtonGroup(back);
    }

    @Override
    public void afterVisible() {
        setupButtonGroup();
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
