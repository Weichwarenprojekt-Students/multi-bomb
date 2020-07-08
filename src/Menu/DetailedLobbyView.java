package Menu;

import Game.Lobby;
import General.MB;
import General.Shared.*;
import Menu.Dialogs.HostPromotion;
import Menu.Dialogs.MapSelection;
import Menu.Dialogs.ModeSelection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

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
     * The list showing the players
     */
    public MBListView<PlayerItem> list;
    /**
     * The player sprites
     */
    private ArrayList<MBImage> playerSprites = new ArrayList<>();
    /**
     * The label showing the title
     */
    private MBLabel title;
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
     * Prevents the view from showing the toasts when joining a lobby
     */
    private boolean firstStart = true;

    /**
     * Constructor
     *
     * @param player   name of the player
     * @param name     of the lobby
     * @param ip       address
     * @param tickRate of the server
     */
    public DetailedLobbyView(String player, String name, String ip, int tickRate) throws IOException {
        super(true);
        this.player = player;
        setupLayout();
        setupLobby(name, ip, tickRate);
    }

    /**
     * Setup the listeners for the lobby
     *
     * @param lobbyName of the lobby
     * @param ip        address
     * @param tickRate  of the server
     */
    public void setupLobby(String lobbyName, String ip, int tickRate) throws IOException {
        setupLobbyEvents();

        // Start the connection
        Lobby.connect(lobbyName, ip, tickRate, player, this);
    }

    /**
     * Setup a lobby event
     */
    public void setupLobbyEvents() {
        // React to a disconnect event
        Lobby.setDisconnectEvent((String message) -> {
            MB.show(new ServerView(), false);
            MB.activePanel.toastError(message);
        });

        // React to lobby changes
        Lobby.setLobbyChangeEvent(new Lobby.LobbyChangeEvent() {
            @Override
            public void playerJoined(String name, int color) {
                list.addItem(new PlayerItem(name, color));
                if (!firstStart) {
                    toastSuccess(name + " joined the lobby!");
                }
            }

            @Override
            public void playerLeft(String name) {
                list.removeItem(name);
                if (!firstStart) {
                    toastError(name + " left the lobby!");
                }
            }

            @Override
            public void hostChanged(String name) {
                changeButtonActivity();
                if (!firstStart) {
                    toastSuccess(name + " is host now!");
                }
                firstStart = false;
            }

            @Override
            public void gameModeChanged(String gameMode) {
                mode.setText("Mode: " + gameMode);
                mode.revalidate();
                mode.repaint();
                if (!firstStart) {
                    toastSuccess("Game mode was", "changed to " + gameMode + "!");
                }
            }
        });
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        title = new MBLabel("Lobby", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 300, 32, 600, 40));

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
        MBBackground background = new MBBackground(new Color(0, 0, 0, 0.2f));
        addComponent(background, () -> background.setBounds(
                scroll.getX() - 4,
                scroll.getY() - 4,
                scroll.getWidth() + 8,
                scroll.getHeight() + 8
        ));

        // The start button
        start = new MBButton("Start");
        start.addActionListener(e -> {
            if (!start.enabled) {
                toastError("Only the host can", "start the game!");
            } else if (Lobby.players.size() <= 1) {
                toastError("You cannot start the game alone!", "Go and get some friends!");
            } else {
                Lobby.startGame();
            }
        });
        addComponent(start, () -> start.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() - 4,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));

        // The change map button
        map = new MBButton("Map: Default");
        map.addActionListener(e -> {
            if (map.enabled) {
                showDialog(new MapSelection(map), () -> {
                });
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
                showDialog(new ModeSelection(), () -> {
                });
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
        leave.addActionListener(e -> {
            Lobby.leave();
            MB.show(new LobbyView(), false);
        });
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

        // Load the player sprites
        playerSprites = MB.getPlayerSprites();

        // Set the lobby name
        title.setText(Lobby.name);

        // Set the button text
        map.setText("Map: " + Lobby.map.name);
        mode.setText("Mode: " + Lobby.mode.name);
        MB.frame.revalidate();
        MB.frame.repaint();
    }

    /**
     * Enable/Disable the configuration buttons
     */
    private void changeButtonActivity() {
        // Check if player is host
        boolean enabled = Lobby.isHost(player);
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
        private final int color;
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
        public PlayerItem(String name, int color) {
            super(name);
            this.color = color;
            setLayout(null);
            nameLabel = new MBLabel(name);
            nameLabel.setBold();
            add(nameLabel);

            // Make the label clickable
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // Check if player is host
                    if (Lobby.isHost(player) && !player.equals(name)) {
                        showDialog(new HostPromotion(name), () -> {
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
                    if (Lobby.isHost(player)) {
                        hovering = true;
                        nameLabel.setFontColor(Color.WHITE);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    nameLabel.setFontColor(MBButton.GREY);
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
            if (playerSprites.size() > color) {
                g.drawImage(playerSprites.get(color).getSub(32, 0, 32, 36), 8, 8, null);
            }

            // Draw the crown if player is host
            if (Lobby.isHost(name)) {
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
