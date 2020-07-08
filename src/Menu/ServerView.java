package Menu;

import General.MB;
import General.Shared.*;
import Menu.Dialogs.ServerManagement;
import Server.Messages.REST.ServerInfo;
import Server.Server;
import Server.ServerList;

import javax.swing.*;
import java.awt.*;

import static Menu.DetailedLobbyView.BUTTON_WIDTH;
import static Menu.DetailedLobbyView.MARGIN;

/**
 * This class provides an lobby overview
 */
public class ServerView extends MBPanel {
    /**
     * The waiting time for next refresh
     */
    public static int REFRESH_TIME = 1000;
    /**
     * The server that can be started locally
     */
    private static Server server;
    /**
     * Selected Server
     */
    private static String selectedServer = "";
    /**
     * Server list Management
     */
    private final ServerList serverList = new ServerList();
    /**
     * Loading Spinner
     */
    public MBSpinner spinner;
    /**
     * List for all shown server
     */
    private MBListView<ServerListItem> listView;
    /**
     * Buttons for navigation
     */
    private MBButton back, host, join, manageRemotes;
    /**
     * Indicate if Thread is running
     * True if running
     */
    private boolean running = false;

    /**
     * Constructor
     */
    public ServerView() {
        super(true);
        setupLayout();
    }


    /**
     * Setup the layout
     */
    public void setupLayout() {
        //The title
        MBLabel title = new MBLabel("Server Overview", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(
                getWidth() / 2 - 300,
                32,
                600,
                40)
        );

        // Scrollable listView
        listView = new MBListView<>();
        MBScrollView scroll = new MBScrollView(listView);
        addComponent(scroll, () -> scroll.setBounds(
                getWidth() / 2 - (getHeight() + MARGIN + BUTTON_WIDTH) / 2,
                96,
                getHeight(),
                getHeight() - 160)
        );

        // Spinner that is shown until server list is created
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

        // Back button
        back = new MBButton("Back");
        back.addActionListener(e -> {
            running = false;
            MB.show(new Menu(), false);
        });
        addComponent(back, () -> back.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + scroll.getHeight() - DetailedLobbyView.BUTTON_HEIGHT + 4,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));

        // Button to host server
        host = new MBButton(Server.running ? "Stop Local Server" : "Host Local Server");
        host.addActionListener(e -> {
            if (!Server.running) {
                showDialog(new MBInputDialog("Enter the server name", MB.settings.serverName, (hostServerName) -> {
                            if (Server.running) {
                                toastError("Cannot start another server");
                            } else {
                                MB.settings.serverName = hostServerName;
                                MB.settings.saveSettings();
                                server = new Server(hostServerName);
                                new Thread(server).start();
                                toastSuccess("Server started");
                                host.setText("Stop Local Server");
                            }
                            MB.activePanel.closeDialog();
                        }),
                        () -> {
                        }
                );
            } else if (server != null) {
                server.closeServer();
                host.setText("Host Local Server");
            } else {
                toastError("Server could not be stopped!");
            }
        });
        addComponent(host, () -> host.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() - 4,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));

        // Button to join selected server
        join = new MBButton("Join");
        join.addActionListener(e -> {
            if (listView.containsItem(selectedServer)) {
                running = false;
                MB.show(new LobbyView(listView.getItem(selectedServer).serverName, selectedServer), false);
            } else {
                toastError("No server selected!");
            }
        });
        addComponent(join, () -> join.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 46,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));

        // Button to add remote server
        manageRemotes = new MBButton("Manage Servers");
        addComponent(manageRemotes, () -> manageRemotes.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 96,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));
        manageRemotes.addActionListener(e -> showDialog(new ServerManagement(), () -> {
        }));

        addButtonGroup(back, host, join, manageRemotes);

    }

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        //Thread for detecting server in local network
        new Thread(() -> {
            running = true;
            while (true) {
                // Search for local servers
                serverList.searchServers();

                // Update ListView
                listView.addMissingItems(serverList.servers.keySet(), ServerView.ServerListItem::new);
                MB.frame.revalidate();
                MB.frame.repaint();
                spinner.setVisible(false);
                back.setVisible(true);

                // Check if Thread still needed
                if (!running) {
                    break;
                }
                try {
                    Thread.sleep(REFRESH_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class ServerListItem extends MBListView.Item {

        /**
         * Label for name
         */
        private final MBLabel nameLabel;
        /**
         * Label for description of the server
         */
        private final MBLabel descriptionLabel;
        /**
         * The name of the server
         */
        public String serverName;

        /**
         * Constructor
         *
         * @param name of the server
         */
        public ServerListItem(String name) {
            super(name);
            setBounds(0, 0, 400, 100);
            setLayout(null);

            // Add the labels
            serverName = serverList.servers.get(name).name;
            nameLabel = new MBLabel(serverName);
            nameLabel.setBold();
            add(nameLabel);
            descriptionLabel = new MBLabel(MBLabel.DESCRIPTION, "");
            updateDescription();
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
            selectedServer = name;
            MB.frame.revalidate();
            MB.frame.repaint();
        }

        /**
         * Update the description of the server item
         */
        private void updateDescription() {
            ServerInfo server = serverList.servers.get(name);
            if (server == null) {
                return;
            }
            descriptionLabel.setText(
                    "Tick-Rate " + server.ticksPerSecond
                            + " \u2022 Lobbies " + server.lobbyCount + "/" + server.maxLobbies
                            + " \u2022 Type " + server.type
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
            if (selectedServer.equals(this.name)) {
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