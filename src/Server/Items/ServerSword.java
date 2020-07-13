package Server.Items;

import General.MultiBomb;

public class ServerSword extends ServerItem {
    /**
     * The name of the item
     */
    public static final String NAME = "Sword";
    /**
     * Time for fade in of the sword
     */
    public static final long FADE_IN_TIME = 200;
    /**
     * Time the sword is spinning for
     */
    public static final long SPINNING_TIME = 500;
    private static final long DELTA_TIME = 100;

    public static void serverLogic(ItemCallback itemCallback, int m, int n) {
        new Thread(() -> {
            MultiBomb.sleep(FADE_IN_TIME);

            for (int i = 0; i <= SPINNING_TIME; i += DELTA_TIME) {
                itemCallback.callback(m, n);
                MultiBomb.sleep(DELTA_TIME);
            }
        }).start();
    }
}
