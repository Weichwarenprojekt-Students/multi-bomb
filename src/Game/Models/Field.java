package Game.Models;

import Game.Battleground;
import General.Shared.MBImage;
import Server.Messages.Socket.Map;

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
    HEART(-3, "Heart", false, true),
    EXPLOSION(-4, "Explosion", false, true);

    /**
     * The width of a field (the actual width would be 300px but the value is slightly increased to avoid white gaps)
     */
    public static int WIDTH = (int) (310f / Map.FIELD_SIZE_PIXELS * Map.FIELD_SIZE);
    /**
     * The height of a field (the actual width would be 450px but the value is slightly increased to avoid white gaps)
     */
    public static int HEIGHT = (int) (465f / Map.FIELD_SIZE_PIXELS * Map.FIELD_SIZE);
    /**
     * The horizontal offset for fields
     */
    public static int offset_x = (int) -((WIDTH - Map.FIELD_SIZE) / 2 * Battleground.ratio);
    /**
     * The vertical offset for fields
     */
    public static int offset_y = (int) -((HEIGHT - Map.FIELD_SIZE) * Battleground.ratio);
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
     * Load the textures of the items
     *
     * @param theme name of the theme
     */
    public static void loadTextures(String theme) {
        // The ground block
        GROUND.image = new MBImage("Maps/" + theme + "/ground.png", false);

        // The other blocks
        SOLID_0.image = new MBImage("Maps/" + theme + "/solid_0.png", false);
        SOLID_1.image = new MBImage("Maps/" + theme + "/solid_1.png", false);
        BREAKABLE_0.image = new MBImage("Maps/" + theme + "/breakable_0.png", false);
        BREAKABLE_1.image = new MBImage("Maps/" + theme + "/breakable_1.png", false);

        // The consumables
        BOMB.image = new MBImage("Items/Consumable/bubble_bomb.png", false);
        SPEED.image = new MBImage("Items/Consumable/bubble_speed.png", false);
        HEART.image = new MBImage("Items/Consumable/bubble_heart.png", false);
        EXPLOSION.image = new MBImage("Items/Consumable/bubble_explosion.png", false);
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

    /**
     * @return all consumable items
     */
    public static byte[] getAllItems() {
        return new byte[] {
                BOMB.id,
                HEART.id,
                SPEED.id,
                EXPLOSION.id
        };
    }
}
