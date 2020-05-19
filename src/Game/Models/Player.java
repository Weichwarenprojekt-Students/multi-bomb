package Game.Models;

import Game.Battleground;
import Game.Game;
import Game.Items.Bomb;
import Game.Items.Item;
import General.MB;
import General.Shared.MBPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * The basic player model
 */
public class Player {
    /**
     * The speed of a player
     */
    public static float SPEED = 45f;
    /**
     * The size of the player sprite
     */
    public static int SPRITE_SIZE = 64;
    /**
     * The sprite of the player
     */
    private final BufferedImage sprite;
    /**
     * The name of the player
     */
    public String name;
    /**
     * The position of the player
     */
    public Position position = new Position();
    /**
     * The theme of the player
     */
    public String theme = "Philipp";
    /**
     * The players item
     */
    private Item item = new Bomb();

    /**
     * Constructor
     */
    public Player() {
        // Load the sprite
        sprite = MB.load("Characters/" + theme + ".png");
    }

    /**
     * Initialize the players position and controls
     *
     * @param panel the game panel
     * @param map   the player is on
     */
    public void initialize(MBPanel panel, Map map) {
        // Set the players position
        position.x = map.spawns[0].x * Map.FIELD_SIZE + (float) Map.FIELD_SIZE / 2;
        position.y = map.spawns[0].y * Map.FIELD_SIZE + (float) Map.FIELD_SIZE / 2;
        position.direction = map.spawns[0].direction;

        // Move upwards
        panel.addKeybinding(
                false,
                "UP",
                (e) -> startMoving(Direction.NORTH),
                KeyEvent.VK_UP,
                KeyEvent.VK_W
        );
        // Stop moving upwards
        panel.addKeybinding(
                true,
                "STOP UP",
                (e) -> Direction.NORTH.moving = false,
                KeyEvent.VK_UP,
                KeyEvent.VK_W
        );

        // Move to the right
        panel.addKeybinding(
                false,
                "RIGHT",
                (e) -> startMoving(Direction.EAST),
                KeyEvent.VK_RIGHT,
                KeyEvent.VK_D
        );

        // Stop moving to the right
        panel.addKeybinding(
                true,
                "STOP RIGHT",
                (e) -> Direction.EAST.moving = false,
                KeyEvent.VK_RIGHT,
                KeyEvent.VK_D
        );

        // Move downwards
        panel.addKeybinding(
                false,
                "DOWN",
                (e) -> startMoving(Direction.SOUTH),
                KeyEvent.VK_DOWN,
                KeyEvent.VK_S
        );

        // Stop moving downwards
        panel.addKeybinding(
                true,
                "STOP DOWN",
                (e) -> Direction.SOUTH.moving = false,
                KeyEvent.VK_DOWN,
                KeyEvent.VK_S
        );

        // Move to the left
        panel.addKeybinding(
                false,
                "LEFT",
                (e) -> startMoving(Direction.WEST),
                KeyEvent.VK_LEFT,
                KeyEvent.VK_A
        );

        // Stop moving to the left
        panel.addKeybinding(
                true,
                "STOP LEFT",
                (e) -> Direction.WEST.moving = false,
                KeyEvent.VK_LEFT,
                KeyEvent.VK_A
        );

        // Use an item
        panel.addKeybinding(
                false,
                "USE ITEM",
                (e) -> useItem(),
                KeyEvent.VK_SPACE
        );
    }

    /**
     * Let the player start moving
     *
     * @param direction of movement
     */
    private void startMoving(Direction direction) {
        position.direction = direction;
        direction.moving = true;
    }

    /**
     * Use the players current item
     */
    private void useItem() {
        item = item.use(position);
    }

    /**
     * Update the players position
     */
    public void update() {
        // Calculate the next position
        float newX = position.x + position.direction.x * Game.deltaTime * SPEED;
        float newY = position.y + position.direction.y * Game.deltaTime * SPEED;

        // Calculate the item on the next field (with some offset for collision detection)
        int m = (int) (newY + position.direction.y * 10) / Map.FIELD_SIZE;
        int n = (int) (newX + position.direction.x * 10) / Map.FIELD_SIZE;

        // Check if character should move
        if (position.direction.moving && Field.getItem(Game.map.fields[m][n]).isPassable()) {
            position.x = newX;
            position.y = newY;
        }
    }

    /**
     * Determine whether the player is on a given field
     *
     * @param m index of the field
     * @param n index of the field
     * @return true if player is on the field
     */
    public boolean isOnField(int m, int n) {
        // Calculate the pixel positions
        int x = n * Map.FIELD_SIZE;
        int y = m * Map.FIELD_SIZE;

        // Check if the player is on the field
        boolean xMatches = position.x >= x && position.x < x + Map.FIELD_SIZE;
        boolean yMatches = position.y >= y && position.y < y + Map.FIELD_SIZE;
        return xMatches && yMatches;
    }

    /**
     * Draw the player
     *
     * @param g the corresponding graphics object
     */
    public void draw(Graphics g) {
        // Calculate the destination position
        int dx = (int) ((position.x - 32) * Battleground.ratio) + Battleground.offset;
        int dy = (int) ((position.y - 55) * Battleground.ratio) + Battleground.offset;

        // Calculate the position of the sprite
        int[] spritePosition = position.direction.getSpritePosition();
        int sx = spritePosition[1] * SPRITE_SIZE;
        int sy = spritePosition[0] * SPRITE_SIZE;

        // Draw the image
        g.drawImage(
                sprite,
                dx,
                dy,
                (int) (SPRITE_SIZE * Battleground.ratio) + dx,
                (int) (SPRITE_SIZE * Battleground.ratio) + dy,
                sx,
                sy,
                sx + SPRITE_SIZE,
                sy + SPRITE_SIZE,
                null
        );
    }
}
