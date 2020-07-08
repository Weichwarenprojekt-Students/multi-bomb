package Menu;

import Editor.Editor;
import General.MB;
import General.Shared.MBButton;
import General.Shared.MBLabel;
import General.Shared.MBPanel;
import General.SettingsOverview;
import General.Shared.MBSlider;
import Server.ServerView;

import javax.swing.*;

/**
 * This class provides a menu for navigating through the game
 */
public class Menu extends MBPanel {

    /**
     * Constructor
     */
    public Menu() {
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBLabel title = new MBLabel("Multi Bomb", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, 50, 200, 40));

        // The button for opening a lobby overview
        MBButton play = new MBButton("Play");
        play.addActionListener(e -> MB.show(new ServerView(), false));
        addComponent(play, () -> play.setBounds(getWidth() / 2 - 70, 100, 140, 40));

        // The button for opening the map editor
        MBButton editor = new MBButton("Editor");
        editor.addActionListener(e -> MB.show(new Editor(), false));
        addComponent(editor, () -> editor.setBounds(getWidth() / 2 - 70, 150, 140, 40));

        // The button for opening the settings
        MBButton settings = new MBButton("Settings");
        settings.addActionListener(e -> MB.show(new SettingsOverview(this), false));
        addComponent(settings, () -> settings.setBounds(getWidth() / 2 - 70, 200, 140, 40));

        // The button for opening a lobby overview
        MBButton exit = new MBButton("Exit");
        exit.addActionListener(e -> System.exit(0));
        addComponent(exit, () -> exit.setBounds(getWidth() / 2 - 70, 250, 140, 40));


        // Setup a button group for navigation
        addButtonGroup(play, editor, settings, exit);
    }

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        setupButtonGroup();
    }
}
