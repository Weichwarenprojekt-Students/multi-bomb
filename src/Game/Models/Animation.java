package Game.Models;

import Game.Game;

/**
 * This enum contains all the required information for the animations
 */
public enum Animation {

    WALK_NORTH(2),
    WALK_EAST(1),
    WALK_SOUTH( 0),
    WALK_WEST( 3);

    /**
     * The measurements of the sprite
     */
    public static final float PLAYER_WIDTH = 320, PLAYER_HEIGHT = 360, SCALE = 1.15f;
    /**
     * The ratio of the measurements
     */
    public static float spriteRatio = 1f;
    /**
     * The time for one animation run
     */
    public static final float MAX_TIME = 0.8f;
    /**
     * The current time counter for the animation
     */
    public float currentTime = 0;
    /**
     * The row in which the corresponding sprite is in
     */
    public int m;
    /**
     * Order of animation
     */
    private final int[] ORDER = {1, 0, 1, 2};

    /**
     * Constructor
     */
    Animation(int m) {
        this.m = m;
    }

    /**
     * Find the right sprite position for the walking animation
     *
     * @param direction of the movement
     */
    public int getN(Direction direction) {
        currentTime += Game.deltaTime;
        if (currentTime > MAX_TIME || !direction.moving) {
            currentTime = 0;
        } else {
            return ORDER[(int) (currentTime / MAX_TIME * ORDER.length)];
        }
        return ORDER[0];
    }
}
