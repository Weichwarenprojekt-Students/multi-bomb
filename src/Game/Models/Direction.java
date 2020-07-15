package Game.Models;

/**
 * The possible directions
 */
public enum Direction {

    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    /**
     * Direction on x axis
     */
    public final int x;
    /**
     * Direction on y axis
     */
    public final int y;

    /**
     * Constructor
     */
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
