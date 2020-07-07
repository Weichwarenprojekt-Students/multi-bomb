package Game.Items;

import Game.Models.Field;
import Game.Models.Upgrades;
import Server.Messages.Socket.Position;

import java.awt.*;

/**
 * The base class for a usable item
 */
public abstract class Item {

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
    public Item(String name, Field field) {
        this.name = name;
        this.field = field;
    }

    /**
     * Handle the use of an item
     */
    public abstract Item use(Position position, Upgrades upgrades);

    /**
     * Draw a used item
     */
    public abstract Item draw(Graphics2D g, int m, int n);

    public interface ItemCallback {
        boolean callback(int[][] position);
    }
}
