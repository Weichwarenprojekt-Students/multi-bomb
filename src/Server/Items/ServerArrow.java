package Server.Items;

import Game.Models.Direction;
import General.MultiBomb;

public class ServerArrow extends ServerItem {
    /**
     * The name of the item
     */
    public static final String NAME = "Arrow";
    /**
     * The time the player needs to stay still and aim the arrow
     */
    public static long AIM_TIME = 1000;
    /**
     * The time the arrow needs to pass one field
     */
    public static long DELTA_TIME = 50;

    public static void serverLogic(ItemCallback itemCallback, int m, int n, Direction direction) {
        new Thread(() -> {
            boolean flying = true;

            // Time for aiming
            MultiBomb.sleep(AIM_TIME);

            for (int i = 1; flying; i++) {
                MultiBomb.sleep(DELTA_TIME);

                flying = !itemCallback.callback(m + (direction.y * i), n + (direction.x * i));
            }
        }).start();
    }
}
