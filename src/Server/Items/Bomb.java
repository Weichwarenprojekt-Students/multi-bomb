package Server.Items;

import Game.Models.Field;

import java.util.logging.Level;

import static General.MultiBomb.LOGGER;

public class Bomb extends Item {
    /**
     * The name of the item
     */
    public static final String NAME = "Bomb";
    /**
     * The time till the bomb detonates in seconds
     */
    public static float DETONATION_TIME = 3f;
    /**
     * The total time in seconds
     */
    public static float TOTAL_TIME = 3.3f;

    /**
     * Constructor
     */
    public Bomb() {
        super(NAME, Field.BOMB);
    }

    /**
     * Run the item logic on the server
     *
     * @param itemCallback callback function that gets passed all fields in a row that might be hit
     * @param m            coordinate on the map
     * @param n            coordinate on the map
     * @param bombSize     the size of the bomb explosion
     */
    public static void serverLogic(ItemCallback itemCallback, int m, int n, int bombSize) {
        // Start new Thread so countdown doesn't block the server
        new Thread(() -> {
            try {
                // wait for the detonation time
                Thread.sleep((long) (DETONATION_TIME * 1000));
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Bomb countdown interrupted", e);
                e.printStackTrace();
            }

            LOGGER.info("Detonate Bomb at m=" + m + ", n=" + n + ", with size=" + bombSize);

            // callback for hitting the position of the bomb
            itemCallback.callback(m, n);

            boolean hit_north = false;
            boolean hit_south = false;
            boolean hit_east = false;
            boolean hit_west = false;

            long delay = (long) ((TOTAL_TIME - DETONATION_TIME) * 1000 / bombSize);

            for (int r = 1; r <= bombSize; r++) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // fill all four rows with the according positions
                if (!hit_north) hit_north = itemCallback.callback(m - r, n);
                if (!hit_south) hit_south = itemCallback.callback(m + r, n);
                if (!hit_east) hit_east = itemCallback.callback(m, n + r);
                if (!hit_west) hit_west = itemCallback.callback(m, n - r);
            }
        }).start();
    }
}
