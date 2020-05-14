package Game;

import Game.Models.Map;
import General.Shared.MBPanel;

/**
 * This class displays and handles the game
 */
public class Game extends MBPanel {

    /**
     * The sidebar
     */
    Sidebar sidebar;
    /**
     * The battleground
     */
    Battleground battleground;

    /**
     * Setup the layout
     */
    @Override
    public void beforeVisible() {
        // Add the sidebar
        sidebar = new Sidebar();
        addComponent(sidebar, () -> sidebar.setBounds(
                (int) (getWidth() / 2 - 0.75 * getHeight()),
                0,
                (int) (0.5 * getHeight()),
                getHeight()
        ));

        // Add the battleground
        battleground = new Battleground(new Map());
        addComponent(battleground, () -> battleground.setBounds(
                (int) (getWidth() / 2 - 0.25 * getHeight()),
                0,
                getHeight(),
                getHeight()
        ));
    }

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        sidebar.afterVisible();
        battleground.afterVisible();
    }
}
