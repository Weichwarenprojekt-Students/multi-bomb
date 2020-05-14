package General;


import General.Shared.MBPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
     * Setup the JFrame and show the menu
     *
     * @param args that were passed
     */
    public static void main(String[] args) {
        MB.settings.loadSettings();
        setupFrame();
        show(new Intro());

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
     */
    public static void show(MBPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
        panel.afterVisible();
    }

    /**
     * Load a image file from the rsc folder via directory/name.
     *
     * @param relativePath
     * @return
     */
    public static BufferedImage load(String relativePath){
        System.out.println(System.getProperty("user.dir"));
        try {
            return ImageIO.read(new File(System.getProperty("user.dir") +
                    File.separator + "rsc" + File.separator + relativePath));
        } catch (IOException e) {
            return null;
        }
    }

}
