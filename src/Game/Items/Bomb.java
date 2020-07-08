package Game.Items;

import Game.Battleground;
import Game.Game;
import Game.Models.Field;
import Game.Models.Map;
import General.MB;
import General.Sound.SoundControl;
import General.Sound.SoundEffect;
import Server.Messages.Position;
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
    public static float DETONATION_TIME = 3f;
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
    public static float BOMB_SIZE = 1;
    /**
     * The horizontal sprite
     */
    private static final MBImage horizontalImage = new MBImage("Items/Bomb/side.png", true);
    /**
     * The left end sprite
     */
    private static final MBImage leftEndImage = new MBImage("Items/Bomb/left_end.png", true);
    /**
     * The right end sprite
     */
    private static final MBImage rightEndImage = new MBImage("Items/Bomb/right_end.png", true);
    /**
     * The top sprite
     */
    private static final MBImage verticalImage = new MBImage("Items/Bomb/top.png", true);
    /**
     * The top end sprite
     */
    private static final MBImage topEndImage = new MBImage("Items/Bomb/top_end.png", true);
    /**
     * The top end sprite
     */
    private static final MBImage bottomEndImage = new MBImage("Items/Bomb/bottom_end.png", true);
    /**
     * The core sprite
     */
    private final MBImage coreImage = new MBImage("Items/Bomb/core.png", true);
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
            bombImage.width = (int) (1.2 * Battleground.fieldSize);
            bombImage.height = (int) (1.2 * Battleground.fieldSize);
        });
        bombImage.rescale((int) (1.2 * Battleground.fieldSize), (int) (1.2 * Battleground.fieldSize));
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

            //Plays the Sound when Bomb is set
            SoundControl.playSoundEffect(SoundEffect.SET_BOMB, false);

            //Plays the explosion sound when bomb detonates
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            SoundControl.playSoundEffect(SoundEffect.SHORT_BOMB_EXPLOSION, false);
                        }
                    },
                    (int) (DETONATION_TIME * 1000)
            );

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
                    n * Battleground.fieldSize + Battleground.offset - (int) (Battleground.ratio * 3),
                    m * Battleground.fieldSize + Battleground.offset - (int) (Battleground.ratio * 8),
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
        float offset = 0.5f;

        // Calculate the first endpoint
        if (firstPercentage < 1) {
            d[0] = (int) ((field + offset - firstPercentage * BOMB_SIZE) * Map.FIELD_SIZE);
        } else {
            d[0] = (int) ((field + offset - percentage * BOMB_SIZE) * Map.FIELD_SIZE);
        }

        // Calculate the second endpoint
        if (secondPercentage < 1) {
            d[1] = (int) ((field + offset + secondPercentage * BOMB_SIZE) * Map.FIELD_SIZE);
        } else {
            d[1] = (int) ((field + offset + percentage * BOMB_SIZE) * Map.FIELD_SIZE);
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
        dx[0] = (int) (dx[0] * Battleground.ratio);
        dx[1] = (int) (dx[1] * Battleground.ratio);
        g.drawImage(
                leftEndImage.image,
                dx[0] - Battleground.fieldSize + Battleground.offset,
                m * Battleground.fieldSize + Battleground.offset,
                null
        );
        g.drawImage(
                horizontalImage.image,
                dx[0] + Battleground.offset ,
                m * Battleground.fieldSize + Battleground.offset,
                dx[1] - dx[0],
                horizontalImage.height,
                null
        );
        g.drawImage(
                rightEndImage.image,
                dx[1] + Battleground.offset ,
                m * Battleground.fieldSize + Battleground.offset,
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
        dy[0] = (int) (dy[0] * Battleground.ratio);
        dy[1] = (int) (dy[1] * Battleground.ratio);
        g.drawImage(
                topEndImage.image,
                n * Battleground.fieldSize + Battleground.offset,
                dy[0] - Battleground.fieldSize + Battleground.offset ,
                null
        );
        g.drawImage(
                verticalImage.image,
                n * Battleground.fieldSize + Battleground.offset,
                dy[0] + Battleground.offset ,
                verticalImage.width,
                dy[1] - dy[0],
                null
        );
        g.drawImage(
                bottomEndImage.image,
                n * Battleground.fieldSize + Battleground.offset,
                dy[1] + Battleground.offset,
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
                (int) ((1 + 0.5 * percentage) * Battleground.fieldSize),
                (int) ((1 + 0.5 * percentage) * Battleground.fieldSize)
        );

        // Draw the core
        g.drawImage(
                coreImage.image,
                (int) ((n - 0.25 * percentage) * Battleground.fieldSize + Battleground.offset) ,
                (int) ((m - 0.25 * percentage) * Battleground.fieldSize + Battleground.offset),
                null
        );
    }
}
