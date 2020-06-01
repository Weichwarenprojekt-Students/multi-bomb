package General;


import General.Shared.MBPanel;
import Server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
     * Setup the JFrame and show the menu
     *
     * @param args that were passed
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            if ("-s".equals(args[0])) {
                new Server().run();
            } else {
                System.out.println("Option not recognized, try -s to start server only");
            }
        } else {
            MB.settings.loadSettings();
            setupFrame();
            show(new Intro(), false);

            // Set fullscreen if necessary
            if (settings.fullscreen) {
                Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
                activeWindow.getGraphicsConfiguration().getDevice().setFullScreenWindow(MB.frame);
            }
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
    }
}
