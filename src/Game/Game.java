package Game;

import General.Shared.MBButton;
import General.Shared.MBLabel;
import General.Shared.MBPanel;
import General.MB;
import Server.LobbyView;

import javax.swing.*;

/**
 * This class displays and handles the game
 */
public class Game extends MBPanel {

    /**
     * Setup the layout
     */
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

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        setupButtonGroup();
    }
}
