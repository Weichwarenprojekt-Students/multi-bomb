package Game.Items;

import Game.Battleground;
import Game.Models.Field;
import Game.Models.Player;
import Game.Models.Upgrades;
import General.Shared.MBImage;
import General.Shared.MBPanel;
import Server.Items.ServerSword;
import Server.Messages.Socket.ItemAction;
import Server.Messages.Socket.Map;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sword extends Item {

    /**
     * Time the sword is spinning for
     */
    public static final long SPINNING_TIME = ServerSword.SPINNING_TIME;
    /**
     * The sword image
     */
    private static MBImage sword;
    /**
     * The timestamp for when the arrow is used
     */
    private long startTime;

    /**
     * Constructor
     */
    public Sword() {
        super(Item.SWORD, Field.SWORD);
        ammunition = 3;
    }

    /**
     * Constructor for call with less ammunition
     *
     * @param ammunition the updated ammunition count
     */
    public Sword(int ammunition) {
        super(Item.SWORD, Field.SWORD);
        this.ammunition = ammunition;
    }

    /**
     * Load the textures for the bomb
     *
     * @param parent the image size depends on
     */
    public static void loadTextures(MBPanel parent) {
        sword = new MBImage("Items/Sword/sword.png", parent, () -> {
            sword.width = (int) (3.7 * Battleground.fieldSize);
            sword.height = (int) (3.7 * Battleground.fieldSize);
        });
        sword.refresh();
    }

    @Override
    public boolean isUsable(int m, int n, Upgrades upgrades) {
        return ammunition > 0 && Map.getItem(m, n) == null;
    }

    @Override
    public Item use(ItemAction action, Player player) {
        this.startTime = System.currentTimeMillis();

        // Block the player's movement and controls
        player.disable();

        // Enable the movement
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (!player.fadingOut) {
                            player.enable();
                        }
                    }
                },
                SPINNING_TIME
        );

        // Add the item to the map so that the battleground can draw it
        Map.setItem(action.m, action.n, this);

        // Decrease the ammunition
        ammunition--;

        return ammunition > 0 ? new Sword(ammunition) : new Bomb();
    }

    @Override
    public Item draw(Graphics2D g, int m, int n) {
        // Update the counter
        long counter = System.currentTimeMillis() - startTime;

        if (counter > SPINNING_TIME) {
            return null;
        } else {
            // Calculate the new rotation
            float angle = (float) counter / 80;

            // Create the rotated picture
            BufferedImage rotatedSword = new BufferedImage(sword.width, sword.height, BufferedImage.TYPE_INT_ARGB);
            AffineTransform transform = new AffineTransform();
            Graphics2D swordGraphics = rotatedSword.createGraphics();
            transform.rotate(angle, (float) sword.width / 2, (float) sword.height / 2);
            swordGraphics.setTransform(transform);
            swordGraphics.drawImage(sword.image, 0, 0, null);
            swordGraphics.dispose();

            // Draw the image
            g.drawImage(
                    rotatedSword,
                    Battleground.offset + n * Battleground.fieldSize + Battleground.fieldSize / 2 - sword.width / 2,
                    Battleground.offset + m * Battleground.fieldSize + Battleground.fieldSize / 2 - sword.height / 2,
                    null
            );
        }
        return this;
    }
}
