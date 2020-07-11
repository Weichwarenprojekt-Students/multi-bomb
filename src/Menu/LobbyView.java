package Menu;

import Game.Lobby;
import General.MB;
import General.MultiBomb;
import General.Shared.*;
import Server.DetectLobby;
import Server.Messages.ErrorMessage;
import Server.Messages.Message;
import Server.Messages.REST.CreateLobby;
import Server.Messages.REST.JoinLobby;
import Server.Messages.REST.LobbyInfo;
import Server.Server;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static Menu.DetailedLobbyView.BUTTON_WIDTH;
import static Menu.DetailedLobbyView.MARGIN;
import static Menu.ServerView.REFRESH_TIME;

/**
 * This class provides an lobby overview
 */
public class LobbyView extends MBPanel {

    /**
     * Max length for a player name
     */
    private static final int MAX_PLAYER_NAME = 12;
    /**
     * Selected lobby
     */
    private static String selectedLobby = "";
    /**
     * Lobby info
     */
    private LobbyInfo lobbyInfo;
    /**
     * List view for lobbies
     */
    private MBListView<LobbyListItem> listView = new MBListView<>();
    /**
     * Spinner
     */
    private MBSpinner spinner;
    /**
     * Buttons
     */
    private MBButton back, join, create;
    /**
     * True if running
     */
    private boolean running = false;

    /**
     * Constructor
     */
    public LobbyView() {
        super(true);
        setupLayout();
    }

    /**
     * Constructor
     *
     * @param name    of the server
     * @param address of the server
     */
    public LobbyView(String name, String address) {
        super(true);
        Lobby.serverName = name;
        Lobby.ipAddress = address;
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBLabel title = new MBLabel(Lobby.serverName, SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(
                getWidth() / 2 - 300,
                32,
                600,
                40)
        );

        // Create scrollable listView
        listView = new MBListView<>();
        MBScrollView scroll = new MBScrollView(listView);
        addComponent(scroll, () -> scroll.setBounds(
                getWidth() / 2 - (getHeight() + MARGIN + BUTTON_WIDTH) / 2,
                96,
                getHeight(),
                getHeight() - 160));

        // Loading spinner
        spinner = new MBSpinner();
        addComponent(spinner, () ->
                spinner.setBounds(
                        scroll.getX() + scroll.getWidth() / 2 - 50,
                        scroll.getY() + scroll.getHeight() / 2 - 50,
                        100,
                        100)
        );

        // The background
        MBBackground background = new MBBackground(new Color(0, 0, 0, 0.2f));
        addComponent(background, () -> background.setBounds(
                scroll.getX() - 4,
                scroll.getY() - 4,
                scroll.getWidth() + 8,
                scroll.getHeight() + 8
        ));

        // Join button
        join = new MBButton("Join");
        join.addActionListener(e -> {
            LobbyInfo.SingleLobbyInfo lobby = lobbyInfo.lobbies.get(selectedLobby);
            if (lobby != null) {
                showDialog(new MBInputDialog("Enter your name", MB.settings.playerName, MAX_PLAYER_NAME, text -> {
                    MB.activePanel.closeDialog();
                    MB.settings.playerName = text;
                    MB.settings.saveSettings();
                    joinLobby(false, lobby.name);
                }), () -> {
                });
            } else {
                toastError("No lobby selected!");
            }
        });

        addComponent(join, () -> join.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() - 4,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));

        // Create button
        create = new MBButton("Create");
        create.addActionListener(e -> {
            //set player name
            showDialog(new MBInputDialog("Enter the lobby name", MB.settings.lobbyName, lobby -> {
                // Close the dialog and save the name
                MB.activePanel.closeDialog();
                MB.settings.lobbyName = lobby;
                MB.settings.saveSettings();
                showDialog(new MBInputDialog("Enter your name", MB.settings.playerName, MAX_PLAYER_NAME, player -> {
                    MB.activePanel.closeDialog();
                    MB.settings.playerName = player;
                    MB.settings.saveSettings();
                    joinLobby(true, lobby);
                }), () -> {
                });
            }), () -> {
            });
        });
        addComponent(create, () -> create.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 46,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));

        // Back button
        back = new MBButton("Back");
        back.addActionListener(e -> {
            running = false;
            MB.show(new ServerView(), false);
        });
        addComponent(back, () -> back.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + scroll.getHeight() - DetailedLobbyView.BUTTON_HEIGHT + 4,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));
        addButtonGroup(back, join, create);
    }


    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        running = true;
        MultiBomb.startTimedAction(REFRESH_TIME, ((deltaTime, totalTime) -> {
            // Get lobby info from server
            lobbyInfo = DetectLobby.getLobbyInfo(Lobby.ipAddress);
            if (lobbyInfo == null) {
                running = false;
                MB.show(new ServerView(), false);
                MB.activePanel.toastError("Server not available!");
                return false;
            }

            // Clear ListView and show new lobbies
            listView.addMissingItems(lobbyInfo.lobbies.keySet(), LobbyListItem::new);

            // Remove the spinner
            spinner.setVisible(false);

            // Repaint everything
            MB.frame.revalidate();
            MB.frame.repaint();

            // Keep the action running
            return running;
        }));
    }

    /**
     * JoinLobby Method
     */
    public void joinLobby(boolean create, String name) {
        try {
            // Setup the request
            HttpURLConnection urlConn;
            URL mUrl = new URL("http://" + Lobby.ipAddress + ":" + Server.HTTP_PORT + "/lobby");
            urlConn = (HttpURLConnection) mUrl.openConnection();
            urlConn.setDoOutput(true);

            if (create) {
                // Create a lobby
                CreateLobby message = new CreateLobby();
                message.playerID = MB.settings.playerName;
                message.lobbyName = name;
                String query = message.toJson();
                urlConn.setRequestProperty("Content-Length", Integer.toString(query.length()));
                urlConn.getOutputStream().write(query.getBytes(StandardCharsets.UTF_8));
            } else {
                // Join a lobby
                JoinLobby message = new JoinLobby();
                message.playerID = MB.settings.playerName;
                message.lobbyName = name;
                String query = message.toJson();
                urlConn.setRequestProperty("Content-Length", Integer.toString(query.length()));
                urlConn.getOutputStream().write(query.getBytes(StandardCharsets.UTF_8));
            }

            // Check if the server responded
            if (urlConn.getResponseCode() == 200) {
                running = false;

                // Show the detailed lobby view
                MB.show(
                        new DetailedLobbyView(
                                MB.settings.playerName,
                                name,
                                Lobby.ipAddress,
                                128
                        ),
                        false
                );
            } else {
                // Show the error message
                String body = new BufferedReader(new InputStreamReader(urlConn.getErrorStream())).readLine();
                ErrorMessage error = (ErrorMessage) Message.fromJson(body);
                toastError(error.error);
            }

            // Close the connection
            urlConn.disconnect();
        } catch (IOException e) {
            MB.show(new ServerView(), false);
            MB.activePanel.toastError("Server communication failed!");
            e.printStackTrace();
        }
    }

    private class LobbyListItem extends MBListView.Item {

        /**
         * Label for lobby name
         */
        MBLabel nameLabel;
        /**
         * Label for lobby description
         */
        MBLabel descriptionLabel;

        /**
         * Constructor
         */
        public LobbyListItem(String name) {
            super(name);
            this.name = name;
            setBounds(0, 0, 400, 100);
            setLayout(null);

            // Setup the name
            nameLabel = new MBLabel(name);
            nameLabel.setBold();
            add(nameLabel);

            // Setup the description
            descriptionLabel = new MBLabel(MBLabel.DESCRIPTION, "");
            add(descriptionLabel);
        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, 50);
            nameLabel.setBounds(12, 6, width - 32, 20);
            descriptionLabel.setBounds(12, 26, width - 32, 20);
        }

        @Override
        public void onSelected() {
            selectedLobby = name;
            MB.frame.revalidate();
            MB.frame.repaint();
        }

        /**
         * Update the description of the lobby item
         */
        private void updateDescription() {
            LobbyInfo.SingleLobbyInfo lobby = lobbyInfo.lobbies.get(name);
            if (lobby == null) {
                return;
            }
            descriptionLabel.setText(
                    "Players " + lobby.players
                    + "/8 \u2022 Game Mode " + lobby.gameMode
                    + " \u2022 Status " + lobby.status
            );
        }

        /**
         * Paint the border
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            MB.settings.enableAntiAliasing(g);
            updateDescription();
            if (selectedLobby.equals(name)) {
                g.setColor(Color.WHITE);
                g.drawRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        MBBackground.CORNER_RADIUS,
                        MBBackground.CORNER_RADIUS
                );
            }
        }
    }
}