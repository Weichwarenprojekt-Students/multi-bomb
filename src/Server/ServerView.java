package Server;

import General.Shared.MBButton;
import General.Shared.MBLabel;
import Menu.Menu;

import General.Shared.MBPanel;
import General.MB;

import javax.swing.*;

/**
 * This class provides an lobby overview
 */
public class ServerView extends MBPanel {

    /**
     * Setup the layout
     */
    @Override
    public void beforeVisible() {
        // The title
        MBLabel title = new MBLabel("Server 1", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, 50, 200, 40));

        // The button for opening a lobby overview
        MBButton play = new MBButton("Join Lobby 1");
        play.addActionListener(e -> MB.show(new LobbyView()));
        addComponent(play, () -> play.setBounds(getWidth() / 2 - 70, 100, 140, 40));

        // The button for opening a lobby overview
        MBButton back = new MBButton("Back");
        back.addActionListener(e -> MB.show(new Menu()));
        addComponent(back, () -> back.setBounds(getWidth() / 2 - 70, 150, 140, 40));

        // Add the buttons to a group
        addButtonGroup(play, back);
    }

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        setupButtonGroup();
    }
}
