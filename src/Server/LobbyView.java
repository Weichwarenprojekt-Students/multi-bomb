package Server;

import Game.Game;
import General.MB;
import General.Shared.MBButton;
import General.Shared.MBLabel;
import General.Shared.MBPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * This class provides an lobby overview
 */
public class LobbyView extends MBPanel {

    /**
     * Constructor
     */
    public LobbyView() {
        super(true);
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBLabel title = new MBLabel("Lobby 1", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, 50, 200, 40));

        // The button for opening a lobby overview
        MBButton play = new MBButton("Play");
        play.addActionListener(e -> MB.show(new Game(""), false));
        addComponent(play, () -> play.setBounds(getWidth() / 2 - 70, 100, 140, 40));

        // The button for opening a lobby overview
        MBButton back = new MBButton("Back");
        back.addActionListener(e -> MB.show(new ServerView(), false));
        addComponent(back, () -> back.setBounds(getWidth() / 2 - 70, 150, 140, 40));
        addKeybinding(
                false,
                "Close LobbyView",
                (e) -> MB.show(new ServerView(), false),
                KeyEvent.VK_ESCAPE
        );

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
