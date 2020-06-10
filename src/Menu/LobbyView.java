package Menu;

import General.Shared.*;

import General.MB;
import Menu.Dialogs.EnterPlayerName;
import Menu.Models.Lobby;
import Server.DetectLobby;
import Server.Messages.LobbyInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static Menu.DetailedLobbyView.BUTTON_WIDTH;
import static Menu.DetailedLobbyView.MARGIN;

/**
 * This class provides an lobby overview
 */
public class LobbyView extends MBPanel {

    /**
     * Address of selected Server
     */
    String serverAddress;
    /**
     * Lobbyinfo
     */
    LobbyInfo lobbyInfo;
    /**
     * Listview for lobbies
     */
    MBListView<LobbyListItem> list;
    /**
     * Spinner
     */
    MBSpinner spinner;

    /**
     * Buttons
     */
    MBButton back, join, create;
    /**
     * True if lobby is selected
     */
    static boolean selected = false;
    /**
     * Selected lobby
     */
    static int selectedLobby;
    /**
     * Player name
     */
    String playerName;


    public LobbyView() {
    }

    public LobbyView (String address) {
        setupLayout();
        serverAddress = address;
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        //The title
        MBLabel title = new MBLabel("Lobby Overview", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(
                getWidth() / 2 - 100,
                32,
                200,
                40));

        //Create scrollable listview
        list = new MBListView<>();
        MBScrollView scroll = new MBScrollView(list);
        addComponent(scroll, () -> scroll.setBounds(
                getWidth() / 2 - (getHeight() + MARGIN + BUTTON_WIDTH) / 2,
                96,
                getHeight(),
                getHeight() - 160));

        //loading spinner
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

        // Join button
        join = new MBButton("Join");
        join.addActionListener( e -> {
            if (selected) {
                Lobby lobby = new Lobby(lobbyInfo.lobbies[selectedLobby].name, "player 1");
                MB.show(new DetailedLobbyView("player 1", lobby, serverAddress), false);
            } else {
                toastError("Keine Lobby", "ausgewählt!");
            }
            selected = false;
        });

        addComponent(join, () -> join.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() - 4,
                DetailedLobbyView.BUTTON_WIDTH,
                DetailedLobbyView.BUTTON_HEIGHT
        ));

        // Create button
        create = new MBButton("Create");
        create.addActionListener( e -> {
            //set player name
            showDialog(new EnterPlayerName(this), () -> {
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
            MB.show(new ServerView(), false);
            selected = false;
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
        //Thread to detect lobbies on server
        new Thread(() -> {
            lobbyInfo = DetectLobby.getLobbyInfo(serverAddress);
            LobbyInfo.SingleLobbyInfo[] lobbies = lobbyInfo.lobbies;

            //Create LobbyListItem for every lobby in LobbyInfo
            for (LobbyInfo.SingleLobbyInfo lobby : lobbies) {
                LobbyListItem listItem = new LobbyListItem(lobby.name, lobby.players, lobby.gameMode, lobby.status);
                list.addItem(listItem);
            }
            //Set button visibility
            spinner.setVisible(false);
            back.setVisible(true);
        }).start();
    }

    public void setPlayerName (String name) {
        this.playerName = name;
    }

    private static class LobbyListItem extends MBListView.Item{
        /**
         * Properties of each lobby
         */
        String name, gameMode, status;
        /**
         * Count of connected players
         */
        int players;
        /**
         * Label for lobby name
         */
        MBLabel nameLabel;
        /**
         * Label for lobby description
         */
        MBLabel descriptionLabel;


        public LobbyListItem(String name, int players, String gameMode, String status) {
            super(name);

            this.name = name;
            this.gameMode = gameMode;
            this.status = status;
            this.players = players;

            setBounds(0,0,400,100);
            setLayout(null);

            //Description for lobby
            String description = "Players " + players + "/8 - Gamemode" + gameMode + " - Status " + status;

            this.nameLabel = new MBLabel(MBLabel.H2, name);
            this.descriptionLabel = new MBLabel(MBLabel.H2, description);
            add(nameLabel);
            add(descriptionLabel);
        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, 150);
            nameLabel.setBounds(20, 10, 400, 40);
            descriptionLabel.setBounds(20, 40, width , 40);
        }

        @Override
        public void onSelected(int index) {
            LobbyView.selected = true;
            LobbyView.selectedLobby = index;
        }
    }
}