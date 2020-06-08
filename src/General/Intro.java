package General;

import General.Shared.MBLabel;
import General.Shared.MBPanel;
import Menu.Menu;

import javax.swing.*;

/**
 * This class shows the game intro and loads settings
 */
public class Intro extends MBPanel {

    /**
     * Constructor
     */
    public Intro() {
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBLabel title = new MBLabel("Intro", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, getHeight() / 2 - 20, 200, 40));
    }

    /**
     * Show the intro and load the settings
     */
    @Override
    public void afterVisible() {
        // Try to wait 2s
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Continue with the menu
        MB.show(new Menu(), false);
    }
}
