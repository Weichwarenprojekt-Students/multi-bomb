package Menu;

import Game.Game;
import General.MB;
import General.Shared.*;
import Menu.Dialogs.HostPromotion;
import Menu.Dialogs.MapSelection;
import Menu.Dialogs.ModeSelection;
import Menu.Models.Lobby;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

public class DetailedLobbyView extends MBPanel {

    /**
     * Distance between buttons and player list
     */
    public static final int MARGIN = 32;
    /**
     * Measurements of the buttons
     */
    public static final int BUTTON_WIDTH = 200, BUTTON_HEIGHT = 40;
    /**
     * The player corresponding to the client
     */
    private final String player;
    /**
     * The lobby
     */
    private final Lobby lobby;
    /**
     * The label showing the title
     */
    private MBLabel title;
    /**
     * The list showing the players
     */
    private MBListView<PlayerItem> list;
    /**
     * The buttons for lobby configuration
     */
    private MBButton start, map, mode;
    /**
     * The leave button
     */
    private MBButton leave;
    /**
     * The crown images
     */
    private MBImage crown, crownChange;
    /**
     * The address of the server
     */
    private String serverAddress;

    /**
     * Constructor
     */
    public DetailedLobbyView(String player, Lobby lobby, String serverAddress) {
        this.player = player;
        this.lobby = lobby;
        this.serverAddress = serverAddress;
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        title = new MBLabel("Lobby", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, 32, 200, 40));

        // The list view
        list = new MBListView<>();
        MBScrollView scroll = new MBScrollView(list);
        addComponent(scroll, () -> scroll.setBounds(
                getWidth() / 2 - (getHeight() + MARGIN + BUTTON_WIDTH) / 2,
                96,
                getHeight(),
                getHeight() - 160
        ));

        // The background
        MBBackground background = new MBBackground(Color.LIGHT_GRAY);
        addComponent(background, () -> background.setBounds(
                    scroll.getX() - 4,
                    scroll.getY() - 4,
                    scroll.getWidth() + 8,
                    scroll.getHeight() + 8
        ));

        // The start button
        start = new MBButton("Start");
        start.addActionListener(e -> {
            if (start.enabled) {
                MB.show(new Game(), false);
            } else {
                toastError("Only the host can", "start the game!");
            }
        });
        addComponent(start, () -> start.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() - 4,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));
        
        // The change map button
        map = new MBButton("Map");
        map.addActionListener(e -> {
            if (map.enabled) {
                showDialog(new MapSelection(lobby, map), () -> {});
            } else {
                toastError("Only the host can", "change the map!");
            }
        });
        addComponent(map, () -> map.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 46,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));
        
        // The change mode button
        mode = new MBButton("Mode");
        mode.addActionListener(e -> {
            if (mode.enabled) {
                showDialog(new ModeSelection(lobby, mode), () -> {});
            } else {
                toastError("Only the host can", "change the mode!");
            }
        });
        addComponent(mode, () -> mode.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 96,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));
        
        // The leave button
        leave = new MBButton("Leave");
        leave.addActionListener(e -> MB.show(new LobbyView(serverAddress), false));
        addComponent(leave, () -> leave.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + scroll.getHeight() - BUTTON_HEIGHT + 4,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));
        addButtonGroup(start, map, mode, leave);
    }

    @Override
    public void afterVisible() {
        setupButtonGroup();
        changeButtonActivity();

        // Load the images
        crown = new MBImage("General/crown.png", () -> {
            crown.width = 35;
            crown.height = 35;
        });
        crownChange = new MBImage("General/crown_change.png", () -> {
            crownChange.width = 35;
            crownChange.height = 35;
        });

        // Set the lobby name
        title.setText(lobby.name);

        // Set the button text
        map.setText("Map: " + lobby.map.name);
        mode.setText("Mode: " + lobby.mode.name);

        // Fill the list with players
        for (Map.Entry<String, Color> entry : lobby.players.entrySet()) {
            list.addItem(new PlayerItem(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Enable/Disable the configuration buttons
     */
    private void changeButtonActivity() {
        // Check if player is host
        boolean enabled = lobby.isHost(player);
        // Enable/Disable buttons
        start.enabled = enabled;
        map.enabled = enabled;
        mode.enabled = enabled;
    }

    private class PlayerItem extends MBListView.Item {
        /**
         * The label showing the name
         */
        private final MBLabel nameLabel;
        /**
         * Color of the player
         */
        private final Color color;
        /**
         * True if mouse is over label
         */
        private boolean hovering = false;
        /**
         * True if the host promotion dialog is open
         */
        private boolean dialog = false;

        /**
         * Constructor
         */
        public PlayerItem(String name, Color color) {
            super(name);
            this.color = color;
            setLayout(null);
            nameLabel = new MBLabel(name);
            add(nameLabel);

            // Make the label clickable
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    // Check if player is host
                    if (player.equals(lobby.host) && !player.equals(name)) {
                        showDialog(new HostPromotion(name, lobby), () -> {
                            dialog = false;
                            changeButtonActivity();
                        });
                        dialog = true;
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (player.equals(lobby.host)) {
                        hovering = true;
                        repaint();
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    repaint();
                }
            });
        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, 50);
            nameLabel.setBounds(48, 16, width - 32, 20);
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
            g.setColor(color);
            g.fillOval(16, 16, 18, 18);
            g.setColor(Color.black);
            g.drawRect(4, 4, getWidth() - 8, getHeight() - 8);

            // Draw the crown if player is host
            if (lobby.host.equals(name)) {
                g.drawImage(crown.image, getWidth() - crown.width - 16, (getHeight() - crown.height) / 2, null);
            } else if (hovering || dialog) {
                g.drawImage(
                        crownChange.image,
                        getWidth() - crownChange.width - 16,
                        (getHeight() - crownChange.height) / 2,
                        null
                );
            }
        }
    }
}
