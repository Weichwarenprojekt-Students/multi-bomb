package Server.Items;

import Game.Models.Field;
import Game.Models.Upgrades;

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
     * The player's upgrades
     */
    private Upgrades upgrades;

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
                e.printStackTrace();
            }

            // initialize arrays for the four rows the bomb hits
            int[][] row1 = new int[bombSize][2];
            int[][] row2 = new int[bombSize][2];
            int[][] row3 = new int[bombSize][2];
            int[][] row4 = new int[bombSize][2];

            // callback for hitting the position of the bomb
            itemCallback.callback(new int[][]{{m, n}});

            for (int r = 1; r <= bombSize; r++) {
                // fill all four rows with the according positions
                row1[r-1] = new int[]{m + r, n};
                row2[r-1] = new int[]{m, n + r};
                row3[r-1] = new int[]{m - r, n};
                row4[r-1] = new int[]{m, n - r};
            }

            // callback for hitting all four rows
            itemCallback.callback(row1);
            itemCallback.callback(row2);
            itemCallback.callback(row3);
            itemCallback.callback(row4);
        }).start();
    }
}
