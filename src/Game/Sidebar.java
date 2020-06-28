package Game;

import Game.Models.Player;
import General.MB;
import General.Shared.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

import static General.Shared.MBLabel.FONT_NAME;

public class Sidebar extends MBPanel {

    /**
     * The padding for the content of the sidebar
     */
    public static final int PADDING = 8;
    /**
     * The player sprites
     */
    private ArrayList<MBImage> playerSprites = new ArrayList<>();
    /**
     * The list view for the user hud
     */
    private final MBListView<PlayerItem> list = new MBListView<>();
    
    /**
     * Constructor
     */
    public Sidebar() {
        super(false);
        setOpaque(false);
        setBackground(null);
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The name of the mode
        MBLabel mode = new MBLabel(MBLabel.H2, Lobby.mode.name);
        addComponent(mode, () -> mode.setBounds(2 * PADDING + 32, PADDING, 200, 32));
        
        // The list view
        int startY = 2 * PADDING + 32;
        MBScrollView scroll = new MBScrollView(list);
        addComponent(scroll, () -> scroll.setBounds(
                PADDING,
                startY, 
                getHeight() / 2 - PADDING,
                getHeight() - startY - PADDING
        ));
    }

    @Override
    public void afterVisible() {
        // The pause button
        MBImageButton pause = new MBImageButton("General/menu.png");
        addComponent(pause, () -> pause.setBounds(PADDING, PADDING, 32, 32));

        // Load the player sprites
        playerSprites = MB.getPlayerSprites();
        
        // Fill the list
        for (Map.Entry<String, Player> player : Lobby.players.entrySet()) {
            list.addItem(new PlayerItem(player.getKey(), player.getValue()));
        }
        // Add the background
        MBBackground background = new MBBackground(MBButton.BACKGROUND_COLOR);
        addComponent(background, () -> background.setBounds(0, 0, getWidth(), getHeight()));
    }

    /**
     * Draw the background
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        MB.settings.enableAntiAliasing(g);
    }

    /**
     * Remove a player
     *
     * @param player name of the player
     */
    public void removePlayer(String player) {
        list.removeItem(player);
    }

    private class PlayerItem extends MBListView.Item {
        /**
         * The font size for the information text
         */
        private final Font font = new  Font(FONT_NAME, Font.BOLD, MBLabel.DESCRIPTION);
        /**
         * The upgrades
         */
        private final MBImageButton heart, explosion, bolt, bomb;
        /**
         * The label showing the name
         */
        private final MBLabel nameLabel;
        /**
         * The player information
         */
        private final Player player;
        /**
         * The width of the item
         */
        private int width = 0;
        /**
         * The measurements of the item
         */
        private final int height = 64, padding = 8, textStart = 48;
        /**
         * Distance between status icons
         */
        private final int iconDistance = 58, iconSize = 22;

        /**
         * Constructor
         */
        public PlayerItem(String name, Player player) {
            super(name);
            this.player = player;
            setLayout(null);

            // Add the name
            nameLabel = new MBLabel(name);
            nameLabel.setBold();
            add(nameLabel);

            // Add the heart image
            heart = new MBImageButton("Items/Consumable/heart.png");
            add(heart);

            // Add the explosion image
            explosion = new MBImageButton("Items/Consumable/explosion.png");
            add(explosion);

            // Add the bolt image
            bolt = new MBImageButton("Items/Consumable/speed.png");
            add(bolt);

            // Add the bomb image
            bomb = new MBImageButton("Items/Consumable/bomb.png");
            add(bomb);
        }

        @Override
        public void onResize(int y, int width) {
            this.width = width;
            setBounds(0, y, width, height);
            nameLabel.setBounds(textStart, padding, width - 32, 20);

            // Position the status icons
            heart.setBounds(width - 2 * iconDistance, padding, iconSize, iconSize);
            explosion.setBounds(width - iconDistance, padding, iconSize, iconSize);
            bolt.setBounds(width - 2 * iconDistance, height - iconSize - padding, iconSize, iconSize);
            bomb.setBounds(width - iconDistance, height - iconSize - padding, iconSize, iconSize);
        }

        @Override
        public void onSelected(int index) {
        }

        /**
         * Paint the border
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            MB.settings.enableAntiAliasing(g);
            if (playerSprites.size() > player.color) {
                g.drawImage(
                        playerSprites.get(player.color).getSub(32, 0, 32, 36),
                        padding,
                        (height - 36) / 2,
                        null
                );
            }

            // Draw the kills and the current item
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString(
                    "Kills: " + player.state.kills,
                    textStart,
                    height - padding - 6
            );
            g.drawString(
                    "Item:",
                    textStart + 80,
                    height - padding - 6
            );
            if (player.item != null) {
                g.drawImage(
                        player.item.field.image.getSub(
                                0,
                                player.item.field.image.height / 3,
                                player.item.field.image.width,
                                player.item.field.image.height
                        ),
                        textStart + 120,
                        height - padding - iconSize - 6,
                        30,
                        45,
                        null
                );
            }

            // Draw the status texts
            int textDistance = 32;
            g.drawString(
                    Integer.toString(player.state.health),
                    width - 2 * iconDistance + textDistance,
                    padding + 15
            );
            g.drawString(
                    Integer.toString(player.state.upgrades.bombSize),
                    width - 2 * iconDistance + textDistance + iconDistance,
                    padding + 15
            );
            g.drawString(
                    Integer.toString(player.state.upgrades.speed),
                    width - 2 * iconDistance + textDistance,
                    height - padding - 6
            );
            g.drawString(
                    Integer.toString(player.state.upgrades.bombCount),
                    width - 2 * iconDistance + textDistance + iconDistance,
                    height - padding - 6
            );
        }
    }
}
