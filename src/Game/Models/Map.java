package Game.Models;

import Game.Items.Item;

/**
 * The model for a map
 */
public class Map {
    /**
     * The size of a field in pixels
     */
    public static float FIELD_SIZE_PIXELS = 256;
    /**
     * The size of a field
     */
    public static int FIELD_SIZE = 30;
    /**
     * The size of the map
     */
    public static int SIZE = 19;
    /**
     * The name of the map
     */
    public String name = "Default";
    /**
     * The spawn points
     */
    public Position[] spawns = new Position[8];
    /**
     * The theme of the map
     */
    public String theme = "Forest";
    /**
     * The battleground
     */
    public byte[][] fields = new byte[SIZE][SIZE];
    /**
     * The items on the battleground
     */
    public Item[][] items = new Item[SIZE][SIZE];

    /**
     * Constructor
     */
    public Map() {
        // Initialize the left and the right side of the field
        for (int m = 0; m < fields.length; m++) {
            // The left and right line
            fields[m][0] = Field.SOLID_0.id;
            fields[m][SIZE - 1] = Field.SOLID_0.id;
        }
        for (int n = 0; n < fields.length; n++) {
            // The top and bottom line
            fields[0][n] = Field.SOLID_0.id;
            fields[SIZE - 1][n] = Field.SOLID_0.id;
        }

        // Initialize the spawn points
        for (int i = 0; i < spawns.length; i++) {
            spawns[i] = new Position();
        }
    }
}
