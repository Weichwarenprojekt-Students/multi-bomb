package Game;

import Game.Models.Player;
import General.MB;
import General.MultiBomb;
import General.Shared.MBPanel;
import General.Shared.MBSpinner;

/**
 * This class displays and handles the game
 */
public class Game extends MBPanel {

    /**
     * The margin of the sidebar
     */
    public static int MARGIN = 16;
    /**
     * The wait time for the targeted refresh rate
     */
    public static long WAIT_TIME = 1000 / MB.settings.refreshRate;
    /**
     * The time difference to the last repaint
     */
    public static long deltaTime;
    /**
     * True if the game is over
     */
    public static boolean gameOver = true;
    /**
     * The battleground
     */
    public Battleground battleground;
    /**
     * The sidebar
     */
    private Sidebar sidebar;

    /**
     * Constructor
     */
    public Game() {
        super(true);
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // React to lobby changes
        Lobby.setLobbyChangeEvent(new Lobby.LobbyChangeEvent() {
            @Override
            public void playerJoined(String name, int color) {
            }

            @Override
            public void playerLeft(String name) {
                toastError(name + " left the lobby!");
                sidebar.removePlayer(name);
                Lobby.lobby.list.removeItem(name);
            }

            @Override
            public void hostChanged(String name) {
                toastSuccess(name + " is host now!");
                Lobby.lobby.changeButtonActivity();
            }

            @Override
            public void gameModeChanged(String gameMode) {
            }
        });
    }

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        // The loading spinner
        MBSpinner spinner = new MBSpinner();
        addComponent(spinner, () -> spinner.setBounds(
                getWidth() / 2 - 50,
                getHeight() / 2 - 50,
                100,
                100
        ));

        // Add the battleground
        battleground = new Battleground(Lobby.map, true, false);
        addComponent(battleground, () -> {
            battleground.setBounds(
                    (int) (getWidth() / 2 - 0.25 * getHeight()) + 2 * MARGIN,
                    2 * MARGIN,
                    getHeight() - 4 * MARGIN,
                    getHeight() - 4 * MARGIN
            );
            battleground.calculateSize();
        });

        // Add the sidebar
        sidebar = new Sidebar();
        addComponent(sidebar, () -> sidebar.setBounds(
                (int) (getWidth() / 2 - 0.75 * getHeight()) + MARGIN,
                MARGIN,
                (int) (1.5 * getHeight()) - 2 * MARGIN,
                getHeight() - 2 * MARGIN
        ));
        sidebar.afterVisible();

        // Show the battleground
        MultiBomb.sleep(1000);
        spinner.setVisible(false);
        battleground.afterVisible();
        MB.frame.repaint();
        MB.frame.revalidate();
    }

    /**
     * Start the game
     */
    public void startGame() {
        // Initialize the player
        for (java.util.Map.Entry<String, Player> player : Lobby.players.entrySet()) {
            player.getValue().initialize(Lobby.player.equals(player.getKey()));
        }

        // Start the game loop
        gameOver = false;
        MultiBomb.startTimedAction(WAIT_TIME, (deltaTime, lastTime) -> {
            Game.deltaTime = deltaTime;

            // Update the player and repaint
            Lobby.players.get(Lobby.player).move();

            // End the game if gameOver is true
            return !gameOver;
        });
        MultiBomb.startTimedAction(WAIT_TIME, (deltaTime, lastTime) -> {
            MB.frame.revalidate();
            MB.frame.repaint();

            // End the game if gameOver is true
            return !gameOver;
        });
    }
}
