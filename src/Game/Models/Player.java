package Game.Models;

import Game.Battleground;
import Game.Game;
import Game.Items.Bomb;
import Game.Items.Item;
import Game.Lobby;
import General.MultiBomb;
import General.Shared.MBImage;
import General.Shared.MBPanel;
import General.Sound.SoundControl;
import General.Sound.SoundEffect;
import Server.Messages.Socket.ItemAction;
import Server.Messages.Socket.Map;
import Server.Messages.Socket.PlayerState;
import Server.Messages.Socket.Position;

import java.awt.*;
import java.awt.event.KeyEvent;

import static Game.Models.Animation.*;

/**
 * The basic player model
 */
public class Player {
    /**
     * The amount of time the player is protected after respawn
     */
    public static int SPAWN_PROTECTION = 3000;
    /**
     * The speed of a player
     */
    public static float SPEED = 0.06f;
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
    public int color;
    /**
     * Opacity of the player
     */
    public float opacity = 1f;
    /**
     * The player state
     */
    public PlayerState state = new PlayerState();
    /**
     * The player's item
     */
    public Item item;
    /**
     * True if the player is controllable
     */
    private boolean controllable = true;
    /**
     * The sprite of the player
     */
    private MBImage sprite;
    /**
     * True if the player is currently using an item
     */
    private boolean usingItem = false;

    /**
     * Constructor
     */
    public Player(String name, int color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Initialize the players position and controls
     *
     * @param controllable true if the player should be controllable
     */
    public void initialize(boolean controllable) {
        // Set the players position
        setSpawn();

        // Initialize the item and the upgrades
        item = new Bomb();
        state = new PlayerState();

        // Load the sprite
        sprite = new MBImage("Characters/" + color + ".png", Lobby.game.battleground, () -> {
            // Update the ratio
            spriteRatio = (float) Battleground.fieldSize / PLAYER_WIDTH;

            // Update the measurements
            sprite.width = (int) (spriteRatio * SCALE * 3 * PLAYER_WIDTH);
            sprite.height = (int) (spriteRatio * SCALE * 4 * PLAYER_HEIGHT);
        });
        sprite.refresh();

        // Setup the players controls
        if (controllable) {
            setupControls(Lobby.game);
        }
    }

    /**
     * Set the spawn
     */
    public void setSpawn() {
        position.x = Lobby.map.spawns[color].x * Map.FIELD_SIZE + (float) Map.FIELD_SIZE / 2;
        position.y = Lobby.map.spawns[color].y * Map.FIELD_SIZE + (float) Map.FIELD_SIZE / 2;
        position.direction = Lobby.map.spawns[color].direction;
    }

    /**
     * Method to disable movement
     */
    public void enableMovement() {
        this.controllable = true;
    }

    /**
     * Method to disable movement
     */
    public void disableMovement() {
        this.controllable = false;
        position.moving = false;
    }

    /**
     * Show visually that the player took a hit
     */
    public void takeHit() {
        int duration = 3000;
        MultiBomb.startTimedAction(Game.WAIT_TIME, (deltaTime, totalTime) -> {
            // Reset the opacity and respawn the player
            if (totalTime > duration) {
                opacity = 1f;
                return false;
            }

            // Reduce the players opacity
            opacity = (float) (0.3 * Math.cos(6 * Math.PI * (duration - totalTime) / duration)) + 0.5f ;
            return true;
        });
    }

    /**
     * Let the player die
     *
     * @param respawn true if the player shall respawn
     */
    public void die(boolean respawn) {
        SoundControl.playSoundEffect(SoundEffect.CHARACTER_DEATH);
        disableMovement();

        int duration = 2000;
        MultiBomb.startTimedAction(Game.WAIT_TIME, (deltaTime, totalTime) -> {
            // Reset the opacity and respawn the player
            if (totalTime > duration) {
                if (respawn) {
                    setSpawn();
                    enableMovement();
                } else {
                    state.health = 0;
                }
                opacity = 1f;
                return false;
            }

            // Reduce the players opacity
            opacity = (float) (duration - totalTime) / duration;
            return true;
        });
    }

    /**
     * Setup the players controls
     *
     * @param panel that is active
     */
    private void setupControls(MBPanel panel) {
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
                (e) -> {
                    if (position.direction == Direction.NORTH) position.moving = false;
                },
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
                (e) -> {
                    if (position.direction == Direction.EAST) position.moving = false;
                },
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
                (e) -> {
                    if (position.direction == Direction.SOUTH) position.moving = false;
                },
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
                (e) -> {
                    if (position.direction == Direction.WEST) position.moving = false;
                },
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
        if (controllable) {
            position.direction = direction;
            position.moving = true;
        }
    }

    /**
     * Use the players current item
     */
    private void useItem() {
        if (!usingItem && controllable) {
            usingItem = true;
            Lobby.sendMessage(new ItemAction(
                    item.name,
                    name,
                    (int) (position.y / Map.FIELD_SIZE),
                    (int) (position.x / Map.FIELD_SIZE)
            ));
        }
    }

    /**
     * Handle an item action
     *
     * @param name of the item
     */
    public void handleItemAction(String name, int m, int n) {
        if (!name.equals(item.name)) {
            item = Item.getItem(name);
        }
        item = item.use(m, n, state.upgrades);
        usingItem = false;
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
        if (position.moving && Field.getItem(Lobby.map.fields[m][n]).isPassable()) {
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
        if (sprite == null || !state.isAlive()) {
            return;
        }
        // Calculate the destination position
        int dx = (int) ((position.x - 18) * Battleground.ratio) + Battleground.offset;
        int dy = (int) ((position.y - 32) * Battleground.ratio) + Battleground.offset;
        // Calculate the position of the sprite
        int[] spritePosition = Animation.getSpritePosition(position, color);
        // Draw the image
        g.drawImage(
                sprite.getSub(
                        (int) (spriteRatio * SCALE * spritePosition[1] * PLAYER_WIDTH),
                        (int) (spriteRatio * SCALE * spritePosition[0] * PLAYER_HEIGHT),
                        (int) (spriteRatio * SCALE * PLAYER_WIDTH),
                        (int) (spriteRatio * SCALE * PLAYER_HEIGHT)
                ),
                dx,
                dy,
                null
        );
    }
}
