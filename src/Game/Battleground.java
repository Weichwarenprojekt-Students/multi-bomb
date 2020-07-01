package Game;

import Game.Models.Field;
import General.Shared.MBBackground;
import General.Shared.MBButton;
import Server.Messages.Socket.Map;
import Game.Models.Player;
import General.MB;
import General.Shared.MBPanel;
import Server.Messages.Socket.Position;

import java.awt.*;

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
    public static int fieldSize = 30;
    /**
     * The map to be drawn
     */
    public static Map map;
    /**
     * True if the players shall be drawn
     */
    private final boolean drawPlayers;
    /**
     * True if the spawns shall be drawn
     */
    private final boolean drawSpawns;
    /**
     * True if the panel should start drawing the battleground
     */
    public boolean startDrawing = false;

    /**
     * Constructor
     *
     * @param map         to be drawn
     * @param drawPlayers true if the player should be drawn
     */
    public Battleground(Map map, boolean drawPlayers, boolean drawSpawns) {
        super(false);
        setOpaque(false);
        Battleground.map = map;
        this.drawPlayers = drawPlayers;
        this.drawSpawns = drawSpawns;

        // Listen for resize events
        MB.activePanel.addComponentEvent(this::calculateSize);
    }

    /**
     * Calculate the size and the offset for the fields
     */
    private void calculateSize() {
        // Calculate the field size
        fieldSize = (int) ((float) getHeight() / Map.SIZE);
        // Calculate the ratio
        ratio = (float) fieldSize / Map.FIELD_SIZE;
        // Calculate the offset
        offset = (getHeight() - fieldSize * Map.SIZE) / 2;
        // Calculate the offset for fields
        Field.offset_x = (int) -((Field.WIDTH - Map.FIELD_SIZE) / 2 * ratio);
        // Calculate the offset for fields
        Field.offset_y = (int) -((Field.HEIGHT - Map.FIELD_SIZE) * ratio);
    }

    /**
     * Executed after panel is visible
     */
    @Override
    public void afterVisible() {
        // Calculate the field sizes
        calculateSize();

        // Load the item textures and repaint
        Field.loadTextures(map.theme);
        MB.activePanel.resize();
        startDrawing = true;
    }

    /**
     * Draw the battleground
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!startDrawing) {
            return;
        }
        MB.settings.enableAntiAliasing(g);

        // Draw the ground
        for (int m = 0; m < Map.SIZE; m++) {
            for (int n = 0; n < Map.SIZE; n++) {
                g.drawImage(
                        Field.GROUND.image.image,
                        n * fieldSize + offset + Field.offset_x,
                        m * fieldSize + offset + Field.offset_y,
                        null
                );
            }
        }

        // Draw the spawns
        if (drawSpawns) {
            for (Position spawn : map.spawns) {
                if (spawn != null) {
                    g.drawImage(
                            Field.SPAWN.image.image,
                            (int) (spawn.x * fieldSize + offset + Field.offset_x),
                            (int) (spawn.y * fieldSize + offset + Field.offset_y),
                            null
                    );
                }
            }
        }

        // Draw the map
        for (int m = 0; m < Map.SIZE; m++) {
            for (int n = 0; n < Map.SIZE; n++) {
                // Identify the field item
                Field field = Field.getItem(map.fields[m][n]);

                // Check if item doesn't exist or is ground
                if (field != null && field.id != Field.GROUND.id) {
                    // Draw the image respecting the images ratio and the required offset
                    g.drawImage(
                            field.image.image,
                            n * fieldSize + offset + Field.offset_x,
                            m * fieldSize + offset + Field.offset_y,
                            null
                    );
                }

                // Check if it should draw the player
                if (drawPlayers) {
                    for (java.util.Map.Entry<String, Player> player : Lobby.players.entrySet()) {
                        if (player.getValue() != null && player.getValue().isOnField(m, n)) {
                            player.getValue().draw(g);
                        }
                    }
                }

                // Draw item above player
                if (Map.items[m][n] != null) {
                    Map.items[m][n] = Map.items[m][n].draw((Graphics2D) g, m, n);
                }
            }
        }

        // Draw the picture frame
        g.setColor(MBButton.GREY);
        int size = 16;
        g.fillRoundRect(0, 0, size, getHeight(), MBBackground.CORNER_RADIUS, MBBackground.CORNER_RADIUS);
        g.fillRoundRect(0, 0, getWidth(), size, MBBackground.CORNER_RADIUS, MBBackground.CORNER_RADIUS);
        g.fillRoundRect(getWidth() - size, 0, size, getHeight(), MBBackground.CORNER_RADIUS, MBBackground.CORNER_RADIUS);
        g.fillRoundRect(0, getHeight() - size, getWidth(), size, MBBackground.CORNER_RADIUS, MBBackground.CORNER_RADIUS);
    }
}
