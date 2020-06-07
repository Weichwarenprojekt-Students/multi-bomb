package Game;

import General.MB;
import General.SettingsOverview;
import General.Shared.MBButton;
import General.Shared.MBLabel;
import General.Shared.MBPanel;
import Server.LobbyView;

import javax.swing.*;
import java.awt.*;

public class Overlay extends MBPanel {

    /**
     * Constructor
     */
    public Overlay() {
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // Change to transparent background
        setBackground(new Color(0, 0, 0, 0.7f));

        // The title
        MBLabel title = new MBLabel("Overlay", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, 50, 200, 40));

        // The button for opening a lobby overview
        MBButton resume = new MBButton("Continue");
        resume.addActionListener(e -> setVisible(false));
        addComponent(resume, () -> resume.setBounds(getWidth() / 2 - 70, 100, 140, 40));

        // The button for opening a lobby overview
        MBButton settings = new MBButton("Settings");
        settings.addActionListener(e -> MB.show(new SettingsOverview(MB.activePanel), false));
        addComponent(settings, () -> settings.setBounds(getWidth() / 2 - 70, 150, 140, 40));

        // The button for opening a lobby overview
        MBButton back = new MBButton("Exit");
        back.addActionListener(e -> {
            Game.gameOver = true;
            MB.show(new LobbyView(), false);
        });
        addComponent(back, () -> back.setBounds(getWidth() / 2 - 70, 200, 140, 40));

        // Add the buttons to a group
        addButtonGroup(settings, back);
    }

    @Override
    public void afterVisible() {
        setupButtonGroup();
    }
}
