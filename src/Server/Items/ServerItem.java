package Server.Items;

import Game.Models.Field;

/**
 * The base class for a usable item
 */
public abstract class ServerItem {

    /**
     * The name of the item
     */
    public final String name;

    /**
     * Constructor
     */
    public ServerItem(String name) {
        this.name = name;
    }

    public interface ItemCallback {
        boolean callback(int m, int n);
    }
}
