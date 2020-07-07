package Game.Models;

public class Upgrades {
    /**
     * The maximum bomb size
     */
    public static final int MAX_BOMB_SIZE = 5;
    /**
     * The maximum bomb count
     */
    public static final int MAX_BOMB_COUNT = 5;
    /**
     * The maximum speed
     */
    public static final int MAX_SPEED = 5;
    /**
     * The radius of the bomb explosion
     */
    public int bombSize = 1;
    /**
     * The amount of bombs the player can place at once
     */
    public int bombCount = 1;
    /**
     * The speed of the player
     */
    public int speed = 1;
}
