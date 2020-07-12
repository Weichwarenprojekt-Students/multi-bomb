package Game.Items;

import Game.Battleground;
import Game.Lobby;
import Game.Models.Direction;
import Game.Models.Field;
import Game.Models.Player;
import Game.Models.Upgrades;
import General.Shared.MBImage;
import General.Shared.MBPanel;
import Server.Items.ServerArrow;
import Server.Messages.Socket.Map;

import java.awt.*;

public class Arrow extends Item {

    /**
     * The time the player needs to stay still and aim the arrow
     */
    public static float AIM_TIME = ServerArrow.AIM_TIME;
    /**
     * The time the arrow needs to pass one field
     */
    public static float DELTA_TIME = ServerArrow.DELTA_TIME;
    /**
     * The left end sprite
     */
    private static MBImage north;
    /**
     * The right end sprite
     */
    private static MBImage east;
    /**
     * The top sprite
     */
    private static MBImage south;
    /**
     * The top end sprite
     */
    private static MBImage west;
    /**
     * The timestamp for when the arrow is used
     */
    private long startTime;
    /**
     * The direction the player was looking when using the arrow's
     */
    private Direction direction;

    /**
     * Constructor
     */
    public Arrow() {
        super(Item.ARROW, Field.ARROW);

        // Set the default ammunition
        ammunition = 3;
    }

    /**
     * Constructor for call with less ammunition
     *
     * @param ammunition the updated ammunition count
     */
    public Arrow(int ammunition) {
        super(Item.ARROW, Field.ARROW);
        this.ammunition = ammunition;
    }

    /**
     * Load the textures for the bomb
     *
     * @param parent the image size depends on
     */
    public static void loadTextures(MBPanel parent) {
        north = new MBImage("Items/Arrow/arrow_north.png", true, parent);
        east = new MBImage("Items/Arrow/arrow_east.png", true, parent);
        south = new MBImage("Items/Arrow/arrow_south.png", true, parent);
        west = new MBImage("Items/Arrow/arrow_west.png", true, parent);
    }

    /**
     * Check if the item is usable
     *
     * @param m        position
     * @param n        position
     * @param upgrades of the player
     * @return true if the item is usable
     */
    @Override
    public boolean isUsable(int m, int n, Upgrades upgrades) {
        return ammunition > 0 && Map.getItem(m, n) == null;
    }

    /**
     * Use the arrow
     *
     * @param m      position
     * @param n      position
     * @param player who used the item
     * @return the same item if there's ammunition left, otherwise a bomb
     */
    @Override
    public Item use(int m, int n, Player player) {
        this.startTime = System.currentTimeMillis();

        // Save the player's direction
        direction = player.position.direction;

        // Add the item to the map so that the battleground can draw it
        Map.setItem(m, n, this);

        // Decrease the ammunition
        ammunition--;

        return ammunition > 0 ? new Arrow(ammunition) : new Bomb();
    }

    /**
     * Draw the item
     *
     * @param g the graphics context
     * @param m position
     * @param n position
     * @return the item if the animation isn't finished yet
     */
    @Override
    public Item draw(Graphics2D g, int m, int n) {
        // Update the counter
        float counter = (float) (System.currentTimeMillis() - startTime) / 1000;

        // Check if player is still in aim phase
        MBImage image = getImage();
        if (counter < AIM_TIME) {
            float percentage = counter / AIM_TIME;
            int offset = (int) (Battleground.fieldSize * (1 - percentage) / 2);
            g.drawImage(
                    image.image,
                    Battleground.offset + Battleground.fieldSize * n + offset,
                    Battleground.offset + Battleground.fieldSize * m + offset,
                    (int) (Battleground.fieldSize * percentage),
                    (int) (Battleground.fieldSize * percentage),
                    null
            );
        } else {
            // Calculate the arrow's progress
            float progress = (counter - AIM_TIME) / DELTA_TIME;

            // Calculate the start position
            int x = Battleground.offset + Battleground.fieldSize * n;
            int y = Battleground.offset + Battleground.fieldSize * m;

            // Calculate the new progress
            x += direction.x * progress * Battleground.fieldSize;
            y += direction.y * progress * Battleground.fieldSize;

            // Check if there's a collision with one field offset so that the arrows actually hit the field
            // The offset is only necessary for direction west and north
            int offsetY = direction.y >= 0 ? 0 : Battleground.fieldSize;
            int offsetX = direction.x >= 0 ? 0 : Battleground.fieldSize;
            int mArrow = (y + offsetY) / Battleground.fieldSize;
            int nArrow = (x + offsetX) / Battleground.fieldSize;
            if (!Field.getItem(Lobby.map.getField(mArrow, nArrow)).isPassable()) {
                return null;
            }

            // Draw the arrow
            g.drawImage(image.image, x, y, null);
        }
        return this;
    }

    /**
     * @return the right image for the direction
     */
    private MBImage getImage() {
        switch (direction) {
            case NORTH:
                return north;
            case EAST:
                return east;
            case SOUTH:
                return south;
            default:
                return west;
        }
    }
}
