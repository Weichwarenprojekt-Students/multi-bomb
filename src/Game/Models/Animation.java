package Game.Models;

import Game.Game;

/**
 * This enum contains all the required information for the animations
 */
public enum Animation {

    WALK_NORTH(0.8f, 8, 9),
    WALK_EAST(0.8f, 11, 9),
    WALK_SOUTH(0.8f, 10, 9),
    WALK_WEST(0.8f, 9, 9);

    /**
     * The time for one animation run
     */
    public float maxTime;
    /**
     * The current time counter for the animation
     */
    public float currentTime = 0;
    /**
     * The row in which the corresponding sprite is in
     */
    public int m;
    /**
     * Amount of pictures in the sprite
     */
    public int amount;

    /**
     * Constructor
     */
    Animation(float maxTime, int m, int amount) {
        this.maxTime = maxTime;
        this.m = m;
        this.amount = amount;
    }

    /**
     * Find the right sprite position for the walking animation
     *
     * @param direction of the movement
     */
    public int getN(Direction direction) {
        currentTime += Game.deltaTime;
        if (currentTime > maxTime || !direction.moving) {
            currentTime = 0;
        } else {
            return (int) (currentTime / maxTime * amount);
        }
        return 0;
    }
}
