package Menu;

import General.Shared.*;
import General.MB;
import Menu.Dialogs.EnterServerAddress;
import Menu.Dialogs.EnterServerName;
import Server.DetectServer;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static Menu.DetailedLobbyView.BUTTON_WIDTH;
import static Menu.DetailedLobbyView.MARGIN;
import Server.Server;
import Server.ScanServerThread;
import Server.ServerList;

/**
 * This class provides an lobby overview
 */
public class ServerView extends MBPanel {
    /**
     * List for all shown server
     */
    MBListView<ServerListItem> listView;

    /**
     * Loading Spinner
     */
    public MBSpinner spinner;
    /**
     * Buttons for navigation
     */
    MBButton back, host, join, addRemote;
    /**
     * True if the host server dialog is open
     */
    private boolean dialog = false;
    /**
     * Selected Server
     */
    static int selectedServer;
    /**
     * True if server is selected
     */
    static boolean selected = false;
    /**
     * Name for self hosted Server
     */
    String hostedServer = "";
    /**
     * Server list Management
     */
    ServerList serverList = new ServerList();
    /**
     * Constructor
     */
    public ServerView() {
        setupLayout();
    }


    /**
     * Setup the layout
     */
    public void setupLayout() {
        //The title
        MBLabel title = new MBLabel("Server Overview", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(
                getWidth() / 2 - 100,
                32,
                200,
                40));


        //Scrollable listview
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
        MBBackground background = new MBBackground(Color.LIGHT_GRAY);
        addComponent(background, () -> background.setBounds(
                scroll.getX() - 4,
                scroll.getY() - 4,
                scroll.getWidth() + 8,
                scroll.getHeight() + 8
        ));


        // Back button
        back = new MBButton("Back");
        back.addActionListener(e -> {MB.show(
                new Menu(), false);
                selected = false;
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
            if (!hostedServer.isEmpty()) {
                new Thread(() -> new Server(hostedServer).run()).start();
                toastSuccess("Server erstellt");
            } else {
                toastError("Servername leer.", "Server wurde nicht", "erstellt");
            }

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
            if (selected) {
                try {
                    MB.show(new LobbyView(serverList.allServerList.get(selectedServer).serverAddress), false);
                } catch (IndexOutOfBoundsException x) {
                    x.printStackTrace();
                    toastError("Server nicht", "erreichbar");
                    MB.show(new ServerView(), false);
                }
                finally {
                    selected = false;
                }
            } else {
                toastError("Kein Server", "ausgewählt!");
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
        addRemote.addActionListener(e -> showDialog(new EnterServerAddress(this) , () -> {
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
            while (true) {
                //search for local servers
                serverList.searchLocalServer();
                //update allServerList
                serverList.updateAllServerList();
                //update ListView
                serverList.updateListView(listView);
                spinner.setVisible(false);
                back.setVisible(true);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setHostedServer(String hostedServer) {
        this.hostedServer = hostedServer;
    }

    public void addRemoteServer (String serverAddress) {
        serverList.addRemoteServer(serverAddress);
    }

    public static class ServerListItem extends MBListView.Item {
        /**
         * Label for name
         */
        MBLabel nameLabel;
        /**
         * Label for description of the server
         */
        MBLabel descriptionLabel;
        /**
         * Server address
         */
        String serverAddress;


        public ServerListItem(String name, String description, String serverAddress) {
            super(name);

            this.serverAddress = serverAddress;

            setBounds(0,0,400,100);
            setLayout(null);

            this.nameLabel = new MBLabel(MBLabel.H2, name);
            this.descriptionLabel = new MBLabel(MBLabel.H2, description);
            add(nameLabel);
            add(descriptionLabel);

        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, 150);
            nameLabel.setBounds(20, 10, 400, 40);
            descriptionLabel.setBounds(20, 60, width , 40);
        }

        @Override
        public void onSelected(int index) {
            ServerView.selectedServer = index;
            ServerView.selected = true;
        }
    }
}