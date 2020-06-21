package Server.Messages.Socket;

import Game.Items.Item;
import Game.Models.Field;
import Server.Messages.Message;
import Server.Messages.Socket.Position;

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
}
