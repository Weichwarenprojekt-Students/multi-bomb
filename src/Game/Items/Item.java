package Game.Items;

import Game.Models.Position;

import java.awt.*;

/**
 * The base class for a usable item
 */
public abstract class Item {

    /**
     * The name of the item
     */
    String name;

    /**
     * Constructor
     */
    public Item(String name) {
        this.name = name;
    }

    /**
     * Handle the use of an item
     */
    public abstract Item use(Position position);

    /**
     * Draw a used item
     */
    public abstract Item draw(Graphics2D g, int m, int n);
}
