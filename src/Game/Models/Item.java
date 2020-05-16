package Game.Models;

import General.MB;

import java.awt.image.BufferedImage;

/**
 * This class contains the information about the items (blocks and consumables)
 */
public enum Item {

    GROUND(0, "Ground", false, false),
    SOLID_0(1, "First Solid", false, false),
    SOLID_1(2, "Second Solid", false, false),
    BREAKABLE_0(3, "First Breakable", true, false),
    BREAKABLE_1(4, "Second Breakable", true, false);

    /**
     * The id of the item
     */
    public byte id;
    /**
     * The name of the item
     */
    public String name;
    /**
     * True if the item is breakable
     */
    public boolean breakable;
    /**
     * True if the item is consumable
     */
    public boolean consumable;
    /**
     * The image of the item
     */
    public BufferedImage image;

    /**
     * Constructor
     */
    Item(int id, String name, boolean breakable, boolean consumable) {
        this.id = (byte) id;
        this.name = name;
        this.breakable = breakable;
        this.consumable = consumable;
    }

    /**
     * Load the textures of the items
     *
     * @param theme name of the theme
     */
    public static void loadTextures(String theme) {
        // The ground block
        GROUND.image = MB.load("Maps/" + theme + "/ground.png");

        // The other blocks
        SOLID_0.image = MB.load("Maps/" + theme + "/solid_0.png");
        SOLID_1.image = MB.load("Maps/" + theme + "/solid_1.png");
        BREAKABLE_0.image = MB.load("Maps/" + theme + "/breakable_0.png");
        BREAKABLE_1.image = MB.load("Maps/" + theme + "/breakable_1.png");
    }

    /**
     * Check if item is passable
     *
     * @return True if the item is passable
     */
    public boolean isPassable() {
        return id <= 0;
    }

    /**
     * Get an item by id
     *
     * @param id of the item
     * @return the right item
     */
    public static Item getItem(byte id) {
        for (Item item : Item.values()) {
            if (item.id == id) {
                return item;
            }
        }
        return SOLID_0;
    }
}
