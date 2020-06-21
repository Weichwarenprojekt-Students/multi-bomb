package Game;

import Game.Models.Player;
import General.MB;
import General.Shared.MBPanel;
import Menu.DetailedLobbyView;
import Menu.Models.Lobby;

import java.awt.event.KeyEvent;

/**
 * This class displays and handles the game
 */
public class Game extends MBPanel {

    /**
     * The wait time for the targeted refresh rate
     */
    public static long WAIT_TIME = 1000 / MB.settings.refreshRate;
    /**
     * The time difference to the last repaint
     */
    public static float deltaTime;
    /**
     * The last time the frame was repainted
     */
    public static long lastTime = System.currentTimeMillis();
    /**
     * True if the game is over
     */
    public static boolean gameOver = true;
    /**
     * The overlay
     */
    public static Overlay overlay;
    /**
     * The name of the user
     */
    private final String player;
    /**
     * The sidebar
     */
    private Sidebar sidebar;
    /**
     * The battleground
     */
    private Battleground battleground;

    /**
     * Constructor
     */
    public Game(String player) {
        this.player = player;
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The button for opening a lobby overview
        overlay = new Overlay();
        overlay.setVisible(false);
        addComponent(overlay, () -> overlay.setBounds(0, 0, getWidth(), getHeight()));
        addKeybinding(
                false,
                "Open Overlay",
                (e) -> overlay.setVisible(!overlay.isVisible()),
                KeyEvent.VK_ESCAPE
        );

        // Add the sidebar
        sidebar = new Sidebar();
        addComponent(sidebar, () -> sidebar.setBounds(
                (int) (getWidth() / 2 - 0.75 * getHeight()),
                0,
                (int) (0.5 * getHeight()),
                getHeight()
        ));

        // React to lobby changes
        Lobby.setLobbyChangeEvent(new Lobby.LobbyChangeEvent() {
            @Override
            public void playerJoined(String name, int color) {
            }

            @Override
            public void playerLeft(String name) {
                toastError(name + " left the lobby!");
            }

            @Override
            public void hostChanged(String name) {
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
        // Add the battleground
        battleground = new Battleground(Lobby.map, true);
        addComponent(battleground, () -> battleground.setBounds(
                (int) (getWidth() / 2 - 0.25 * getHeight()),
                0,
                getHeight(),
                getHeight()
        ));

        // Call the after visible methods
        sidebar.afterVisible();
        battleground.afterVisible();
    }

    /**
     * Start the game
     */
    public void startGame() {
        // Initialize the player
        for (java.util.Map.Entry<String, Player> player : Lobby.players.entrySet()) {
            player.getValue().initialize(this, this.player.equals(player.getKey()));
        }

        // Start the game loop
        gameOver = false;
        while (!gameOver) {
            // Update the global times
            deltaTime = (float) (System.currentTimeMillis() - lastTime) / 1000;
            lastTime = System.currentTimeMillis();

            // Record the start time
            long start = System.currentTimeMillis();

            // Update the player and repaint
            Lobby.players.get(this.player).update();
            MB.frame.revalidate();
            MB.frame.repaint();

            // Wait for the next run
            targetRefreshRate(start);
        }
    }

    /**
     * Target the refresh rate by waiting for the next run
     *
     * @param start time in milliseconds
     */
    private void targetRefreshRate(long start) {
        long localDelta = System.currentTimeMillis() - start;
        if (localDelta < WAIT_TIME) {
            try {
                Thread.sleep(WAIT_TIME - localDelta);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
