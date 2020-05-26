package Game.Items;

import Game.Battleground;
import Game.Game;
import Game.Models.Field;
import Game.Models.Map;
import Game.Models.Position;
import General.Shared.MBImage;

import java.awt.*;

public class Bomb extends Item {
    /**
     * The name of the item
     */
    public static String NAME = "Bomb";
    /**
     * The time till the bomb detonates in seconds
     */
    public static float DETONATION_TIME = 3;
    /**
     * The total time in seconds
     */
    public static float TOTAL_TIME = 3.3f;
    /**
     * The count of bombs
     */
    public static float BOMB_COUNT = 1;
    /**
     * The size of the bomb detonation
     */
    public static float BOMB_SIZE = 5;
    /**
     * The horizontal sprite
     */
    private static final MBImage horizontalImage = new MBImage("Items/Bomb/side.png");
    /**
     * The left end sprite
     */
    private static final MBImage leftEndImage = new MBImage("Items/Bomb/left_end.png");
    /**
     * The right end sprite
     */
    private static final MBImage rightEndImage = new MBImage("Items/Bomb/right_end.png");
    /**
     * The top sprite
     */
    private static final MBImage verticalImage = new MBImage("Items/Bomb/top.png");
    /**
     * The top end sprite
     */
    private static final MBImage topEndImage = new MBImage("Items/Bomb/top_end.png");
    /**
     * The top end sprite
     */
    private static final MBImage bottomEndImage = new MBImage("Items/Bomb/bottom_end.png");
    /**
     * The core sprite
     */
    private final MBImage coreImage = new MBImage("Items/Bomb/core.png");
    /**
     * The bomb sprite
     */
    private static MBImage bombImage;
    /**
     * The counter for the detonation
     */
    private float counter = 0;
    /**
     * The maximum possible range of the bomb on the east
     */
    private float percentageEast = 1;
    /**
     * The maximum possible range of the bomb on the south
     */
    private float percentageSouth = 1;
    /**
     * The maximum possible range of the bomb on the west
     */
    private float percentageWest = 1;
    /**
     * The maximum possible range of the bomb on the north
     */
    private float percentageNorth = 1;

    /**
     * Constructor
     */
    public Bomb() {
        super(NAME);
    }

    /**
     * Reset the bomb upgrades
     */
    public static void reset() {
        BOMB_COUNT = 1;
        BOMB_SIZE = 1;

        // Initialize the bomb sprite
        bombImage = new MBImage("Items/Bomb/bomb.png", () -> {
            bombImage.width = (int) (1.2 * Battleground.size);
            bombImage.height = (int) (1.2 * Battleground.size);
        });
        bombImage.rescale((int) (1.2 * Battleground.size), (int) (1.2 * Battleground.size));
    }

    /**
     * Use the bomb
     *
     * @param position of the player
     * @return a new bomb
     */
    @Override
    public Item use(Position position) {
        // Calculate the players position
        int m = (int) (position.y / Map.FIELD_SIZE);
        int n = (int) (position.x / Map.FIELD_SIZE);

        // Check if the player is able to place a bomb
        if (BOMB_COUNT > 0 && Game.map.items[m][n] == null) {

            // Add the item to the map so that the battleground can draw it
            Game.map.items[m][n] = this;

            // Decrease the bomb count
            BOMB_COUNT--;
        }
        // Use the item
        return new Bomb();
    }

    /**
     * Draw the bomb animation
     *
     * @param g graphics context
     * @param m position on the y axis
     * @param n position on the x axis
     * @return the bomb or null if the bomb exploded
     */
    @Override
    public Item draw(Graphics2D g, int m, int n) {
        // Update the counter
        counter += Game.deltaTime;

        // Check if bomb is still there
        if (counter < DETONATION_TIME) {
            // Draw the image
            g.drawImage(
                    bombImage.image,
                    n * Battleground.size + Battleground.offset - (int) (Battleground.ratio * 4),
                    m * Battleground.size + Battleground.offset - (int) (Battleground.ratio * 7),
                    null
            );
        } else if (counter < TOTAL_TIME) {
            // Calculate the percentage and change the opacity
            float percentage = (counter - DETONATION_TIME) / (TOTAL_TIME - DETONATION_TIME);
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (1 - percentage));
            g.setComposite(ac);

            // Draw the animations
            drawHorizontalExplosion(g, m, n, percentage);
            drawVerticalExplosion(g, m, n, percentage);

            // Reset the opacity
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            drawCore(g, m, n, percentage);
        } else {
            BOMB_COUNT++;
            return null;
        }
        return this;
    }

    /**
     * Calculate the endpoints of the explosion
     *
     * @param field            the bomb was planted
     * @param percentage       of the detonation
     * @param firstPercentage  of the first endpoint
     * @param secondPercentage of the second endpoint
     * @return the endpoint values
     */
    private int[] calculateEndpoints(int field, float percentage, float firstPercentage, float secondPercentage) {
        int[] d = new int[2];
        float offset = 0.65f;

        // Calculate the first endpoint
        if (firstPercentage < 1) {
            d[0] = (int) ((field + offset) * Map.FIELD_SIZE - firstPercentage * BOMB_SIZE * Map.FIELD_SIZE);
        } else {
            d[0] = (int) ((field + offset) * Map.FIELD_SIZE - percentage * BOMB_SIZE * Map.FIELD_SIZE);
        }

        // Calculate the second endpoint
        if (secondPercentage < 1) {
            d[1] = (int) ((field + offset) * Map.FIELD_SIZE + secondPercentage * BOMB_SIZE * Map.FIELD_SIZE);
        } else {
            d[1] = (int) ((field + offset) * Map.FIELD_SIZE + percentage * BOMB_SIZE * Map.FIELD_SIZE);
        }
        return d;
    }

    /**
     * Draw the horizontal part of the explosion
     *
     * @param g          graphics context
     * @param m          position on the y axis
     * @param n          position on the x axis
     * @param percentage of the progress of the explosion
     */
    private void drawHorizontalExplosion(Graphics g, int m, int n, float percentage) {
        // Calculate the explosion range
        int[] dx = calculateEndpoints(n, percentage, percentageWest, percentageEast);

        // Check if a solid block is reached
        boolean reachedSolid = dx[0] < 0 || !Field.getItem(Game.map.fields[m][dx[0] / Map.FIELD_SIZE]).isPassable();
        if (percentageWest >= 1 && reachedSolid) {
            percentageWest = percentage;
        }
        reachedSolid = dx[1] / Map.FIELD_SIZE > Map.SIZE ||
                !Field.getItem(Game.map.fields[m][dx[1] / Map.FIELD_SIZE]).isPassable();
        if (percentageEast >= 1 && reachedSolid) {
            percentageEast = percentage;

        }

        // Draw the images
        g.drawImage(
                leftEndImage.image,
                (int) (dx[0] * Battleground.ratio) - Battleground.size,
                m * Battleground.size + Battleground.offset,
                null
        );
        g.drawImage(
                horizontalImage.image,
                (int) (dx[0] * Battleground.ratio),
                m * Battleground.size + Battleground.offset,
                (int) ((dx[1] - dx[0]) * Battleground.ratio),
                horizontalImage.height,
                null
        );
        g.drawImage(
                rightEndImage.image,
                (int) (dx[1] * Battleground.ratio),
                m * Battleground.size + Battleground.offset,
                null
        );
    }

    /**
     * Draw the horizontal part of the explosion
     *
     * @param g          graphics context
     * @param m          position on the y axis
     * @param n          position on the x axis
     * @param percentage of the progress of the explosion
     */
    private void drawVerticalExplosion(Graphics g, int m, int n, float percentage) {
        // Calculate the explosion range
        int[] dy = calculateEndpoints(m, percentage, percentageNorth, percentageSouth);

        // Check if a solid block is reached
        boolean reachedSolid = dy[0] < 0 || !Field.getItem(Game.map.fields[dy[0] / Map.FIELD_SIZE][n]).isPassable();
        if (percentageNorth >= 1 && reachedSolid) {
            percentageNorth = percentage;
        }
        reachedSolid = dy[1] / Map.FIELD_SIZE > Map.SIZE ||
                !Field.getItem(Game.map.fields[dy[1] / Map.FIELD_SIZE][n]).isPassable();
        if (percentageSouth >= 1 && reachedSolid) {
            percentageSouth = percentage;

        }

        // Draw the images
        g.drawImage(
                topEndImage.image,
                n * Battleground.size + Battleground.offset,
                (int) (dy[0] * Battleground.ratio) - Battleground.size,
                null
        );
        g.drawImage(
                verticalImage.image,
                n * Battleground.size + Battleground.offset,
                (int) (dy[0] * Battleground.ratio),
                verticalImage.width,
                (int) ((dy[1] - dy[0]) * Battleground.ratio),
                null
        );
        g.drawImage(
                bottomEndImage.image,
                n * Battleground.size + Battleground.offset,
                (int) (dy[1] * Battleground.ratio),
                null
        );
    }

    /**
     * Draw the core of the explosion
     *
     * @param g          graphics context
     * @param m          position on the y axis
     * @param n          position on the x axis
     * @param percentage of the progress of the explosion
     */
    private void drawCore(Graphics g, int m, int n, float percentage) {
        // Rescale the image
        coreImage.rescale(
                (int) ((1 + 0.5 * percentage) * Battleground.size),
                (int) ((1 + 0.5 * percentage) * Battleground.size)
        );

        // Draw the core
        g.drawImage(
                coreImage.image,
                (int) ((n - 0.25 * percentage) * Battleground.size + Battleground.offset),
                (int) ((m - 0.25 * percentage) * Battleground.size + Battleground.offset),
                null
        );
    }
}
