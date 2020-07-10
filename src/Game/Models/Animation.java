package Game.Models;

import Game.Game;
import Server.Messages.Socket.Position;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This enum contains all the required information for the animations
 */
public enum Animation {

    WALK_NORTH(2),
    WALK_EAST(1),
    WALK_SOUTH(0),
    WALK_WEST(3);

    /**
     * The measurements of the sprite
     */
    public static final float PLAYER_WIDTH = 320, PLAYER_HEIGHT = 360, SCALE = 1.15f;
    /**
     * The time for one animation run
     */
    public static final long MAX_TIME = 600;
    /**
     * The ratio of the measurements
     */
    public static float spriteRatio = 1f;
    /**
     * Order of animation
     */
    private final int[] ORDER = {1, 0, 1, 2};
    /**
     * The current time counter for the animation
     */
    public long[] currentTimes = new long[]{0, 0, 0, 0, 0, 0, 0, 0};
    /**
     * The row in which the corresponding sprite is in
     */
    public int m;

    /**
     * Constructor
     */
    Animation(int m) {
        this.m = m;
    }


    /**
     * Calculate the right sprite position for the walking direction
     *
     * @param position of the player
     * @param index    of the player
     * @return the positions [m, n]
     */
    public static int[] getSpritePosition(Position position, int index) {
        int[] positions = new int[2];
        switch (position.direction) {
            case NORTH:
                positions[0] = Animation.WALK_NORTH.m;
                positions[1] = Animation.WALK_NORTH.getN(position, index);
                break;

            case EAST:
                positions[0] = Animation.WALK_EAST.m;
                positions[1] = Animation.WALK_EAST.getN(position, index);
                break;

            case SOUTH:
                positions[0] = Animation.WALK_SOUTH.m;
                positions[1] = Animation.WALK_SOUTH.getN(position, index);
                break;

            case WEST:
                positions[0] = Animation.WALK_WEST.m;
                positions[1] = Animation.WALK_WEST.getN(position, index);
                break;
        }
        return positions;
    }

    /**
     * Find the right sprite position for the walking animation
     *
     * @param position of the player
     * @param index    of the player
     */
    private int getN(Position position, int index) {
        currentTimes[index] += Game.deltaTime;
        if (currentTimes[index] > MAX_TIME || !position.moving) {
            currentTimes[index] = 0;
        } else {
            int animation = (int) ((float) currentTimes[index] / MAX_TIME * ORDER.length);
            return ORDER[animation < ORDER.length ? animation : ORDER.length - 1];
        }
        return ORDER[0];
    }
}
