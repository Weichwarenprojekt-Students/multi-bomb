package Game.Models;

/**
 * The model for a map
 */
public class Map {
    /**
     * The size of the map
     */
    public static int SIZE = 19;
    /**
     * The name of the map
     */
    public String name = "Default";
    /**
     * The theme of the map
     */
    public String theme = "Forest";
    /**
     * The battleground
     */
    public byte[][] fields = new byte[SIZE][SIZE];

    /**
     * Constructor
     */
    public Map() {
        // Initialize the left and the right side of the field
        for (int m = 0; m < fields.length; m++) {
            // The left and right line
            fields[m][0] = Item.SOLID_0.id;
            fields[m][SIZE - 1] = Item.SOLID_0.id;
        }
        for (int n = 0; n < fields.length; n++) {
            // The top and bottom line
            fields[0][n] = Item.SOLID_0.id;
            fields[SIZE - 1][n] = Item.SOLID_0.id;
        }
    }
}
