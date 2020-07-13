package Server.Items;

import General.MultiBomb;
import Server.Models.Player;

public class ServerProtection extends ServerItem {
    /**
     * The name of the item
     */
    public static final String NAME = "Protection";
    /**
     * The time the players takes to die
     */
    public static final long DIE_DURATION = 2000;
    /**
     * The time the players is protected after a hit
     */
    public static final long HIT_DURATION = 2000;
    /**
     * Standard protection time
     */
    public static long STANDARD_DURATION = 3000;

    public static void serverLogic(long time, Player player) {
        new Thread(() -> {
            System.out.println("PROTECT!!!");
            player.protect(true);
            System.out.println(("PROTECT???"));
            MultiBomb.sleep(time);
            player.protect(false);
            System.out.println("PROTECTEND");
        }).start();
    }

    public static void serverLogic(Player player) {
        serverLogic(STANDARD_DURATION, player);
    }
}
