package Game.Models;

import Game.Battleground;
import Game.Game;
import Game.Items.Arrow;
import Game.Items.Bomb;
import Game.Items.Item;
import Game.Lobby;
import General.MultiBomb;
import General.Shared.*;
import General.Sound.SoundControl;
import General.Sound.SoundEffect;
import Server.Messages.Socket.*;

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
    public static final int SPAWN_PROTECTION = 3000;
    /**
     * The font for the name
     */
    public static final Font nameFont = new Font(MBLabel.FONT_NAME, Font.BOLD, 14);
    /**
     * The maximum speed value for a player
     */
    public static final float MAX_SPEED = 0.1f, MIN_SPEED = 0.06f;
    /**
     * The current speed value for a player
     */
    public float speed = MIN_SPEED;
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
    public Item item = new Bomb();
    /**
     * True if the player is on an item
     */
    private final Item.OnItem onItem = new Item.OnItem();
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
     */
    public void initialize() {
        // Set the players position
        setSpawn();

        // Load the sprite
        sprite = new MBImage("Characters/" + color + ".png", Lobby.game.battleground, () -> {
            // Update the ratio
            spriteRatio = (float) Battleground.fieldSize / PLAYER_WIDTH;

            // Update the measurements
            sprite.width = (int) (spriteRatio * SCALE * 3 * PLAYER_WIDTH);
            sprite.height = (int) (spriteRatio * SCALE * 4 * PLAYER_HEIGHT);
        });
        sprite.refresh();
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
    public void enable() {
        this.controllable = true;
    }

    /**
     * Method to disable movement
     */
    public void disable() {
        this.controllable = false;
        position.moving = false;
    }

    /**
     * @return the ammunition count matching the current item
     */
    public int getAmmunition() {
        if (item.name.equals(Item.BOMB)) {
            return state.upgrades.bombCount;
        }
        return item.ammunition;
    }

    /**
     * Update the speed of the player
     */
    private void updateSpeed() {
        speed = (MAX_SPEED - MIN_SPEED) * state.upgrades.speed / Upgrades.MAX_SPEED + MIN_SPEED;
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
            opacity = (float) (0.3 * Math.cos(6 * Math.PI * (duration - totalTime) / duration)) + 0.5f;
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
        disable();

        int duration = 2000;
        MultiBomb.startTimedAction(Game.WAIT_TIME, (deltaTime, totalTime) -> {
            // Reset the opacity and respawn the player
            if (totalTime > duration) {
                if (respawn) {
                    setSpawn();
                    enable();
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
    public void setupControls(MBPanel panel) {
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
        int m = (int) (position.y / Map.FIELD_SIZE);
        int n = (int) (position.x / Map.FIELD_SIZE);
        if (!usingItem && controllable && item.isUsable(m, n, state.upgrades)) {
            usingItem = true;
            Lobby.sendMessage(new ItemAction(item.name, name, position.direction, m, n));
        }
    }

    /**
     * Handle an item action
     *
     * @param action the item action
     */
    public void handleItemAction(ItemAction action) {
        if (!action.itemId.equals(item.name)) {
            item = Item.getItem(action.itemId);
        }
        item = item.use(action, this);
        usingItem = false;
    }

    /**
     * Handle an item collection
     *
     * @param item that was collected
     */
    public void handleItemCollection(ItemCollected item) {
        // Check if the player state should be updated
        state.collectItem(item.item, false);
        updateSpeed();

        // Check if the player collected a weapon
        if (item.item.name.equals(Field.ARROW.name)) {
            this.item = new Arrow();
        }
    }

    /**
     * Check if player is on item
     *
     * @param m position
     * @param n position
     */
    public void isOnItem(int m, int n) {
        // Calculate the item on the next field
        int mPlayer = (int) (position.y) / Map.FIELD_SIZE;
        int nPlayer = (int) (position.x) / Map.FIELD_SIZE;

        // Check it and update position
        boolean samePosition = (mPlayer == m && nPlayer == n);
        onItem.onItem = onItem.onItem || samePosition;
        if (samePosition) {
            onItem.setPosition(m, n);
        }
    }

    /**
     * Update the players position
     */
    public void move() {
        // Calculate the next position
        float newX = position.x + position.direction.x * Game.deltaTime * speed;
        float newY = position.y + position.direction.y * Game.deltaTime * speed;

        // Calculate the item on the next field (with some offset for collision detection)
        int m = (int) (newY + position.direction.y * 10) / Map.FIELD_SIZE;
        int n = (int) (newX + position.direction.x * 10) / Map.FIELD_SIZE;
        // Check if character should move
        if (position.moving && Field.getItem(Lobby.map.getField(m, n)).isPassable()
            && Item.isPassable(onItem, m, n)) {
            // Update the position
            position.x = newX;
            position.y = newY;

            // Update on item state
            m = (int) (position.y) / Map.FIELD_SIZE;
            n = (int) (position.x) / Map.FIELD_SIZE;
            onItem.onItem = Map.getItem(m, n) != null && onItem.m == m && onItem.n == n;
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

        // Create the sub image
        Image image = sprite.getSub(
                (int) (spriteRatio * SCALE * spritePosition[1] * PLAYER_WIDTH),
                (int) (spriteRatio * SCALE * spritePosition[0] * PLAYER_HEIGHT),
                (int) (spriteRatio * SCALE * PLAYER_WIDTH),
                (int) (spriteRatio * SCALE * PLAYER_HEIGHT)
        );

        // Draw the image
        g.drawImage(image, dx, dy, null);

        // Fill rect
        g.setFont(nameFont);
        g.setColor(MBButton.BACKGROUND_COLOR);
        g.fillRoundRect(
                dx + (image.getWidth(null) - g.getFontMetrics().stringWidth(name)) / 2 - 2,
                dy - 18,
                g.getFontMetrics().stringWidth(name) + 4,
                16,
                MBBackground.CORNER_RADIUS,
                MBBackground.CORNER_RADIUS
        );

        // Draw the name
        g.setColor(Color.WHITE);
        g.drawString(
                name,
                dx + (image.getWidth(null) - g.getFontMetrics().stringWidth(name)) / 2,
                dy - 5
        );
    }
}
