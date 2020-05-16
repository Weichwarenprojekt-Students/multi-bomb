package Game;

import Game.Models.Item;
import Game.Models.Map;
import Game.Models.Player;
import General.MB;
import General.Shared.MBPanel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Battleground extends MBPanel {
    /**
     * The ratio of the battleground compared to the design size
     */
    public static float ratio = 0;
    /**
     * The offset to the top and to the left of the panel
     */
    public static int offset = 0;
    /**
     * The size of field
     */
    private int size = 30;
    /**
     * The map to be drawn
     */
    private final Map map;
    /**
     * The player to be drawn
     */
    private final Player player;

    /**
     * Constructor
     *
     * @param map to be drawn
     */
    public Battleground(Map map, Player player) {
        this.map = map;
        this.player = player;

        // Load the item textures
        Item.loadTextures(map.theme);

        // Listen for resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateSize();
            }
        });
    }

    /**
     * Calculate the size and the offset for the fields
     */
    private void calculateSize() {
        // Calculate the field size
        size = (int) ((float) getHeight() / Map.SIZE);
        // Calculate the ratio
        ratio = (float) size / Map.FIELD_SIZE;
        // Calculate the offset
        offset = (getHeight() - size * Map.SIZE) / 2;
    }

    @Override
    public void beforeVisible() {
    }

    @Override
    public void afterVisible() {
        calculateSize();
        repaint();
    }

    /**
     * Draw the battleground
     *
     * @param g the corresponding graphics
     */
    public void paint(Graphics g) {
        super.paint(g);
        MB.settings.enableAntiAliasing(g);

        // Draw the ground
        for (int m = 0; m < Map.SIZE; m++) {
            for (int n = 0; n < Map.SIZE; n++) {
                g.drawImage(Item.GROUND.image, n * size + offset, m * size + offset, size, size, null);
            }
        }

        // Draw the map
        for (int m = 0; m < Map.SIZE; m++) {
            for (int n = 0; n < Map.SIZE; n++) {
                // Identify the field item
                Item item = Item.getItem(map.fields[m][n]);

                // Check if item doesn't exist or is ground
                if (item != null && item.id != Item.GROUND.id) {
                    // Calculate the images ratio
                    float ratio = (float) item.image.getHeight() / item.image.getWidth();

                    // Draw the image respecting the images ratio and the required offset
                    g.drawImage(
                            item.image,
                            n * size + offset,
                            (int) ((m + 1 - ratio) * size + offset),
                            size,
                            (int) (size * ratio),
                            null
                    );
                }

                // Check if it should draw the player
                if (player.isOnField(m, n)) {
                    player.draw(g);
                }
            }
        }
    }
}
