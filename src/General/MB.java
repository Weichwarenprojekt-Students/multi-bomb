package General;


import Editor.MapManager;
import General.Shared.MBImage;
import General.Shared.MBPanel;
import General.Sound.SoundControl;
import General.Sound.SoundEffect;
import Menu.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
     * The game background
     */
    public static MBImage background;
    /**
     * A players sprite
     */
    private static MBImage playerSprite;

    /**
     * Setup the JFrame and show the menu
     */
    public static void startGame() {
        MB.settings.loadSettings();
        SoundControl.playLoop(SoundControl.MENU_LOOP, SoundEffect.IN_GAME);
        MapManager.loadMaps();
        background = new MBImage("General/background.png");
        setupFrame();
        MB.show(new Menu(), false);

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
