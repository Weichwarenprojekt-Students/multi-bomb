package Menu;

import General.Shared.*;


import General.MB;
import Server.DetectServer;


import javax.swing.*;


import java.awt.*;


import static Menu.DetailedLobbyView.BUTTON_WIDTH;
import static Menu.DetailedLobbyView.MARGIN;

/**
 * This class provides an lobby overview
 */
public class ServerView extends MBPanel {
    /**
     * List for all shown server
     */
    MBListView<ServerListItem> list;

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
        list = new MBListView<>();
        MBScrollView scroll = new MBScrollView(list);
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
                MB.show(new LobbyView(DetectServer.serverList.get(selectedServer).serverAddress), false);
                selected = false;
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

        addButtonGroup(back, host, join, addRemote);

    }

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {

        //Thread for detecting server in local network
        new Thread(() -> {
            DetectServer.search();
            DetectServer.serverList.forEach(server -> list.addItem(server));
            spinner.setVisible(false);
            back.setVisible(true);
        }).start();

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
         * Serveraddress
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