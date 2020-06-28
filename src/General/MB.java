package General;


import Game.Game;
import General.Shared.MBImage;
import Menu.DetailedLobbyView;
import General.Shared.MBPanel;
import Server.LobbyView;
import Server.Messages.REST.CreateLobby;
import Server.Messages.REST.JoinLobby;
import Server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MB {

    /**
     * The window
     */
    public static JFrame frame = new JFrame("Multi-Bomb");
    /**
     * The general settings
     */
    public static Settings settings = new Settings();
    /**
     * The shown panel
     */
    public static MBPanel activePanel;
    /**
     * A players sprite
     */
    private static MBImage playerSprite;
    /**
     * The game background
     */
    public static MBImage background;

    /**
     * Setup the JFrame and show the menu
     */
    public static void startGame(boolean create, String name) {
        MB.settings.loadSettings();
        background = new MBImage("General/background.png");
        setupFrame();

        try {
            HttpURLConnection urlConn;
            String ip = "92.60.38.195";
            URL mUrl = new URL("http://" + ip + ":" + Server.HTTP_PORT + "/lobby");
            urlConn = (HttpURLConnection) mUrl.openConnection();
            urlConn.addRequestProperty("Content-Type", "application/" + "GET");
            urlConn.setDoOutput(true);
            String lobbyName = "TestLobby";
            if (create) {
                CreateLobby message = new CreateLobby();
                message.playerID = name;
                message.lobbyName = lobbyName;
                String query = message.toJson();
                urlConn.setRequestProperty("Content-Length", Integer.toString(query.length()));
                urlConn.getOutputStream().write(query.getBytes(StandardCharsets.UTF_8));
            } else {
                JoinLobby message = new JoinLobby();
                message.playerID = name;
                message.lobbyName = lobbyName;
                String query = message.toJson();
                urlConn.setRequestProperty("Content-Length", Integer.toString(query.length()));
                urlConn.getOutputStream().write(query.getBytes(StandardCharsets.UTF_8));
            }
            if (urlConn.getResponseCode() == 200) {
                show(new DetailedLobbyView(name, lobbyName, ip, 128), false);
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            show(new LobbyView(), false);
            e.printStackTrace();
        }

        // Set fullscreen if necessary
        if (settings.fullscreen) {
            Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
            activeWindow.getGraphicsConfiguration().getDevice().setFullScreenWindow(MB.frame);
        }
    }

    /**
     * Setup the frame
     */
    private static void setupFrame() {
        frame.setBounds(settings.x, settings.y, settings.width, settings.height);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1060, 720));

        // Listen for window measurement changes and save them
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                if (!settings.fullscreen) {
                    settings.x = e.getComponent().getX();
                    settings.y = e.getComponent().getY();
                    settings.saveSettings();
                }
            }

            @Override
            public void componentResized(ComponentEvent e) {
                if (!settings.fullscreen) {
                    settings.width = e.getComponent().getWidth();
                    settings.height = e.getComponent().getHeight();
                    settings.saveSettings();
                }
            }
        });
    }

    /**
     * Show a panel and remove the old one
     *
     * @param panel to be showed
     * @param known true if the panel was already shown
     */
    public static void show(MBPanel panel, boolean known) {
        activePanel = panel;
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
        if (!known) {
            panel.afterVisible();
        }
        // Make sure the background is drawn
        panel.repaint();
    }

    /**
     * @return all player sprites
     */
    public static ArrayList<MBImage> getPlayerSprites() {
        ArrayList<MBImage> playerSprites = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            playerSprite = new MBImage("Characters/" + i + ".png", () -> {
                playerSprite.width = 96;
                playerSprite.height = 144;
            });
            playerSprite.refresh();
            playerSprites.add(playerSprite);
        }
        return playerSprites;
    }
}
