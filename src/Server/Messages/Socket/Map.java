package Server.Messages.Socket;

import Game.Items.Item;
import Game.Models.Field;
import Server.Messages.Message;

/**
 * The model for a map
 */
public class Map extends Message {
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
     * The description for a custom map
     */
    public static String CUSTOM = "Custom";
    /**
     * The items on the battleground
     */
    public static Item[][] items = new Item[SIZE][SIZE];
    /**
     * The name of the map
     */
    public String name = "New Map";
    /**
     * The description of the map
     */
    public String description = CUSTOM;
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
     * Constructor
     */
    public Map() {
        // Initialize Map as message
        super(Message.MAP_TYPE);

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
        spawns[0] = new Position(1, 1);
        spawns[1] = new Position(SIZE - 2, 1);
        spawns[2] = new Position(1, SIZE - 2);
        spawns[3] = new Position(SIZE - 2, SIZE - 2);
        spawns[4] = new Position(4, 4);
        spawns[5] = new Position(SIZE - 5, 4);
        spawns[6] = new Position(4, SIZE - 5);
        spawns[7] = new Position(SIZE - 5, SIZE - 5);
    }

    /**
     * Copy a field
     */
    public static Map copy(Map map) {
        Map copy = new Map();

        // Copy the fields
        for (int m = 0; m < SIZE; m++) {
            System.arraycopy(map.fields[m], 0, copy.fields[m], 0, SIZE);
        }

        // Copy the spawns
        System.arraycopy(map.spawns, 0, copy.spawns, 0, map.spawns.length);

        // Copy the name
        copy.name = map.name;

        return copy;
    }

    /**
     * @return true if the map is custom
     */
    public boolean isCustom() {
        return description.equals(CUSTOM);
    }

    /**
     * @return true if all the spawns are set
     */
    public boolean allSpawnsSet() {
        for (Position spawn : spawns) {
            if (spawn == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the amount of spawns
     */
    public int countSpawns() {
        int amount = 0;
        for (Position spawn : spawns) {
            if (spawn != null) {
                amount++;
            }
        }
        return amount;
    }

    /**
     * Set the next spawn point
     *
     * @return true if it was successful
     */
    public boolean setSpawn(int m, int n) {
        for (int i = 0; i < spawns.length; i++) {
            if (spawns[i] == null) {
                spawns[i] = new Position(n, m);
                return true;
            }
        }
        return false;
    }
}
