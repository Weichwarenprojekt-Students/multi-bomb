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
     * True if the player is moving in the corresponding direction
     */
    public boolean moving = false;

    /**
     * Constructor
     */
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calculate the right sprite position for the walking direction
     *
     * @return the positions [m, n]
     */
    public int[] getSpritePosition() {
        int[] positions = new int[2];
        switch (this) {
            case NORTH:
                positions[0] = Animation.WALK_NORTH.m;
                positions[1] = Animation.WALK_NORTH.getN(this);
                break;

            case EAST:
                positions[0] = Animation.WALK_EAST.m;
                positions[1] = Animation.WALK_EAST.getN(this);
                break;

            case SOUTH:
                positions[0] = Animation.WALK_SOUTH.m;
                positions[1] = Animation.WALK_SOUTH.getN(this);
                break;

            case WEST:
                positions[0] = Animation.WALK_WEST.m;
                positions[1] = Animation.WALK_WEST.getN(this);
                break;
        }
        return positions;
    }
}
