package Game.Items;

import Game.Models.Field;
import Game.Models.Upgrades;
import General.Shared.MBPanel;

import java.awt.*;

/**
 * The base class for a usable item
 */
public abstract class Item {
    /**
     * The available items
     */
    public static final String BOMB = "Bomb";
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
     * Load the textures for all the items
     *
     * @param parent the image size depends on
     */
    public static void loadTextures(MBPanel parent) {
        Bomb.loadTextures(parent);
    }

    /**
     * Get an item by the name
     *
     * @param name of the item
     * @return the corresponding item
     */
    public static Item getItem(String name) {
        return new Bomb();
    }


    /**
     * Handle the use of an item
     */
    public abstract Item use(int m, int n, Upgrades upgrades);

    /**
     * Draw a used item
     */
    public abstract Item draw(Graphics2D g, int m, int n);
}
