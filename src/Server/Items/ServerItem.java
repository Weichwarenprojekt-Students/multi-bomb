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
     * The field that matches the item
     */
    public final Field field;

    /**
     * Constructor
     */
    public ServerItem(String name, Field field) {
        this.name = name;
        this.field = field;
    }

    public interface ItemCallback {
        boolean callback(int m, int n);
    }
}
