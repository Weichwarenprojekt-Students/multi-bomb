package Game;

import Game.Models.Map;
import Game.Models.Player;
import General.MB;
import General.Shared.MBPanel;

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
     * The sidebar
     */
    private Sidebar sidebar;
    /**
     * The battleground
     */
    private Battleground battleground;
    /**
     * The player that the client is controlling
     */
    private Player player;
    /**
     * The map
     */
    private Map map;

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
        map = new Map();
        player = new Player();
        battleground = new Battleground(map, player);
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
        // Call the after visible methods
        sidebar.afterVisible();
        battleground.afterVisible();

        // Start game in new thread
        new Thread(this::startGame).start();
    }

    /**
     * Start the game
     */
    public void startGame() {
        // Initialize the player
        player.initialize(this, map);

        // Start the game loop
        gameOver = false;
        while (!gameOver) {
            // Update the global times
            deltaTime = (float) (System.currentTimeMillis() - lastTime) / 1000;
            lastTime = System.currentTimeMillis();

            // Record the start time
            long start = System.currentTimeMillis();

            // Update the player and repaint
            player.update();
            battleground.repaint();

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
