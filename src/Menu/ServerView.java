package Menu;

import General.MB;
import General.Shared.*;
import Menu.Dialogs.EnterServerAddress;
import Menu.Dialogs.EnterServerName;
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
     * Selected Server
     */
    private static String selectedServerAddress = "";
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
    private MBButton back, host, join, addRemote;
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
                getWidth() / 2 - 150,
                32,
                300,
                40));


        //Scrollable listView
        listView = new MBListView<>();
        MBScrollView scroll = new MBScrollView(listView);
        addComponent(scroll, () -> scroll.setBounds(
                getWidth() / 2 - (getHeight() + MARGIN + BUTTON_WIDTH) / 2,
                96,
                getHeight(),
                getHeight() - 160));


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
        host = new MBButton("Host");
        host.addActionListener(e -> showDialog(new EnterServerName(this), () -> {
        }));
        addComponent(host, () -> host.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() - 4,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));

        // Button to join selected server
        join = new MBButton("Join");
        join.addActionListener(e -> {
            if (!selectedServerAddress.isEmpty()) {
                running = false;
                MB.show(new LobbyView(selectedServerAddress), false);
            } else {
                toastError("No server", "selected");
            }
        });
        addComponent(join, () -> join.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 46,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));

        //Button to add remote server
        addRemote = new MBButton("Add Remote");
        addComponent(addRemote, () -> addRemote.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 96,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));
        addRemote.addActionListener(e -> showDialog(new EnterServerAddress(this), () -> {
        }));

        addButtonGroup(back, host, join, addRemote);

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
                //search for local servers
                serverList.searchLocalServer();
                //update allServerList
                serverList.updateAllServerList();
                //update ListView
                serverList.updateListView(listView);
                spinner.setVisible(false);
                back.setVisible(true);
                //check if Thread still needed
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

    /**
     * Host a server locally
     *
     * @param hostServerName name of the server
     */
    public void hostServer(String hostServerName) {
        if (!hostServerName.isEmpty()) {
            new Thread(() -> new Server(hostServerName).run()).start();
            toastSuccess("Server started");
        } else {
            toastError("Server name empty");
        }

    }

    /**
     * Add a remote server
     *
     * @param serverAddress address of the server
     */
    public void addRemoteServer(String serverAddress) {
        serverList.addRemoteServer(serverAddress);
    }

    public static class ServerListItem extends MBListView.Item {
        /**
         * Server address
         */
        public String serverAddress;
        /**
         * Label for name
         */
        MBLabel nameLabel;
        /**
         * Label for description of the server
         */
        MBLabel descriptionLabel;

        /**
         * Constructor
         *
         * @param name          of the server
         * @param description   of the server
         * @param serverAddress server address
         */
        public ServerListItem(String name, String description, String serverAddress) {
            super(serverAddress);
            this.serverAddress = serverAddress;

            setBounds(0, 0, 400, 100);
            setLayout(null);

            // Add the labels
            nameLabel = new MBLabel(name);
            nameLabel.setBold();
            add(nameLabel);
            descriptionLabel = new MBLabel(MBLabel.DESCRIPTION, description);
            add(descriptionLabel);

        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, 50);
            nameLabel.setBounds(12, 6, width - 32, 20);
            descriptionLabel.setBounds(12, 26, width - 32, 20);
        }

        @Override
        public void onSelected(int index) {
            ServerView.selectedServerAddress = serverAddress;
            MB.frame.revalidate();
            MB.frame.repaint();
        }

        /**
         * Paint the border
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            MB.settings.enableAntiAliasing(g);
            if (ServerView.selectedServerAddress.equals(serverAddress)) {
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