package Menu;

import Editor.MapSelection;
import General.MB;
import General.Shared.MBButton;
import General.Shared.MBPanel;
import General.Shared.MBTitle;

/**
 * This class provides a menu for navigating through the game
 */
public class Menu extends MBPanel {

    /**
     * The general margin value for the outer components
     */
    public static int MARGIN = 64;
    /**
     * The start position for the buttons
     */
    public static int START_Y = 180;

    /**
     * Constructor
     */
    public Menu() {
        super(true);
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBTitle title = new MBTitle("Multi Bomb");
        addComponent(title, () -> title.setBounds(
                (getWidth() - title.getWidth()) / 2,
                MARGIN,
                title.getWidth(),
                title.getHeight())
        );

        // The button for opening a lobby overview
        int width = 250, height = 40, margin = 16;
        MBButton play = new MBButton("Play");
        play.addActionListener(e -> MB.show(new ServerView(), false));
        addComponent(play, () -> play.setBounds((getWidth() - width) / 2, START_Y, width, height));

        // The button for opening the map editor
        MBButton editor = new MBButton("Editor");
        editor.addActionListener(e -> MB.show(new MapSelection(), false));
        addComponent(editor, () -> editor.setBounds(
                (getWidth() - width) / 2,
                START_Y + height + margin,
                width,
                height
        ));

        // The button for opening the settings
        MBButton settings = new MBButton("Settings");
        settings.addActionListener(e -> MB.show(new SettingsOverview(this), false));
        addComponent(settings, () -> settings.setBounds(
                (getWidth() - width) / 2,
                START_Y + 2 * (height + margin),
                width,
                height
        ));

        // The button for opening a lobby overview
        MBButton exit = new MBButton("Exit");
        exit.addActionListener(e -> System.exit(0));
        addComponent(exit, () -> exit.setBounds(
                (getWidth() - width) / 2,
                START_Y + 3 * (height + margin),
                width,
                height
        ));


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
