package Game.Models;

import Game.Battleground;
import General.Shared.MBImage;

/**
 * This class contains the information about the items (blocks and consumables)
 */
public enum Field {

    GROUND(0, "Ground", false, false),
    SOLID_0(1, "First Solid", false, false),
    SOLID_1(2, "Second Solid", false, false),
    BREAKABLE_0(3, "First Breakable", true, false),
    BREAKABLE_1(4, "Second Breakable", true, false),
    BOMB(-1, "Bomb", false, true),
    SPEED(-2, "Speed", false, true),
    HEART(-3, "Heart", false, true);

    /**
     * The ratio for map fields
     */
    public static final float RATIO = 1.5f;
    /**
     * The horizontal offset
     */
    private static final float OFFSET = (278f / 256 - 1);
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
    public MBImage image;

    /**
     * Constructor
     */
    Field(int id, String name, boolean breakable, boolean consumable) {
        this.id = (byte) id;
        this.name = name;
        this.breakable = breakable;
        this.consumable = consumable;
    }

    /**
     * @return the horizontal offset for fields
     */
    public static int offsetX() {
        return (int) (OFFSET * Battleground.size);
    }

    /**
     * @return the vertical offset for fields
     */
    public static int offsetY() {
        return (int) ((RATIO - 1) * Battleground.size + offsetX());
    }

    /**
     * Load the textures of the items
     *
     * @param theme name of the theme
     */
    public static void loadTextures(String theme) {
        // The ground block
        GROUND.image = new MBImage("Maps/" + theme + "/ground.png");

        // The other blocks
        SOLID_0.image = new MBImage("Maps/" + theme + "/solid_0.png", () -> {
            SOLID_0.image.width = (int) ((OFFSET + 1) * Battleground.size);
            SOLID_0.image.height = (int) (RATIO * SOLID_0.image.width);
        });
        SOLID_1.image = new MBImage("Maps/" + theme + "/solid_1.png", () -> {
            SOLID_1.image.width = (int) ((OFFSET + 1) * Battleground.size);
            SOLID_1.image.height = (int) (RATIO * SOLID_0.image.width);
        });
        BREAKABLE_0.image = new MBImage("Maps/" + theme + "/breakable_0.png", () -> {
            BREAKABLE_0.image.width = (int) ((OFFSET + 1) * Battleground.size);
            BREAKABLE_0.image.height = (int) (RATIO * SOLID_0.image.width);
        });
        BREAKABLE_1.image = new MBImage("Maps/" + theme + "/breakable_1.png", () -> {
            BREAKABLE_1.image.width = (int) ((OFFSET + 1) * Battleground.size);
            BREAKABLE_1.image.height = (int) (RATIO * SOLID_0.image.width);
        });

        // The consumables
        BOMB.image = new MBImage("Items/Consumable/bubble_bomb.png");
        SPEED.image = new MBImage("Items/Consumable/bubble_speed.png");
        HEART.image = new MBImage("Items/Consumable/bubble_heart.png");
    }

    /**
     * Get an item by id
     *
     * @param id of the item
     * @return the right item
     */
    public static Field getItem(byte id) {
        for (Field field : Field.values()) {
            if (field.id == id) {
                return field;
            }
        }
        return SOLID_0;
    }

    /**
     * Check if item is passable
     *
     * @return True if the item is passable
     */
    public boolean isPassable() {
        return id <= 0;
    }
}
