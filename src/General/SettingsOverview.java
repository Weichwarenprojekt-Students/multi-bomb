package General;

import General.Shared.MBButton;
import General.Shared.MBLabel;
import General.Shared.MBPanel;
import Game.Game;
import General.Shared.MBSlider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * This class provides some settings like sound and graphics
 */
public class SettingsOverview extends MBPanel {
    /**
     * The button to change the screen state
     */
    private MBButton fullscreen;
    /**
     * The button to change the anti aliasing state
     */
    private MBButton antiAliasing;
    /**
     * The button to change the refresh rate
     */
    private MBButton refreshRate;

    private MBButton mute;
    /**
     * The last panel that was open
     */
    private final MBPanel last;

    /**
     * Constructor
     *
     * @param last panel that was visible
     */
    public SettingsOverview(MBPanel last) {
        this.last = last;
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBLabel title = new MBLabel("Settings", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 100, 50, 200, 40));

        // The label for window mode
        MBLabel fullscreenLabel = new MBLabel("Window Mode:");
        addComponent(fullscreenLabel, () -> fullscreenLabel.setBounds(getWidth() / 2 - 156, 100, 140, 30));

        // The button for leaving or entering fullscreen
        fullscreen = new MBButton(MB.settings.fullscreen ? "Fullscreen" : "Window");
        fullscreen.addActionListener(e -> changeScreen());
        addComponent(fullscreen, () -> fullscreen.setBounds(getWidth() / 2 + 16, 100, 140, 30));

        // The label for anti aliasing
        MBLabel antiAliasingLabel = new MBLabel("Anti Aliasing:");
        addComponent(antiAliasingLabel, () -> antiAliasingLabel.setBounds(getWidth() / 2 - 156, 140, 140, 30));

        // The button for activating or deactivating anti aliasing
        antiAliasing = new MBButton(MB.settings.antiAliasing ? "Activated" : "Deactivated");
        antiAliasing.addActionListener(e -> changeAntiAliasing());
        addComponent(antiAliasing, () -> antiAliasing.setBounds(getWidth() / 2 + 16, 140, 140, 30));

        // The label for anti aliasing
        MBLabel refreshRateLabel = new MBLabel("Refresh Rate:");
        addComponent(refreshRateLabel, () -> refreshRateLabel.setBounds(getWidth() / 2 - 156, 180, 140, 30));

        // The button for activating or deactivating anti aliasing
        refreshRate = new MBButton(Integer.toString(MB.settings.refreshRate));
        refreshRate.addActionListener(e -> changeRefreshRate());
        addComponent(refreshRate, () -> refreshRate.setBounds(getWidth() / 2 + 16, 180, 140, 30));

        // The label for muting the audio
        MBLabel muteLabel = new MBLabel("Mute Audio:");
        addComponent(muteLabel, () -> muteLabel.setBounds(getWidth() / 2 - 156, 220, 140, 30));


        // The button for muting the udio
        mute = new MBButton("Mute");
        //mute.addActionListener(e -> MB.settings.sound.muteAudio());
        mute.addActionListener(e -> {
            if (mute.getButtonText().equals("Mute")) {
                MB.settings.sound.muteAudio();
                mute.setText("Unmute");
            }
            else if (mute.getButtonText().equals("Unmute")) {
                MB.settings.sound.unmuteAudio();
                mute.setText("Mute");
            }
        });
        addComponent(mute, () -> mute.setBounds(getWidth() / 2 + 16, 220, 140, 30));

        // The label for muting the audio
        MBLabel volumeLabel = new MBLabel("Change Volume:");
        addComponent(volumeLabel, () -> volumeLabel.setBounds(getWidth() / 2 - 156, 260, 140, 30));

        // The slider to handle the game volume
        MBSlider volumeslider = new MBSlider(MB.settings.sound.getVolume(), percentage -> {
            MB.settings.sound.changeVolume(percentage);
            if (percentage==0) {
                mute.setText("Unmute");
            }
            else if (percentage==100) {
                mute.setText("Mute");
            }
            //System.out.println(percentage);
        });
        addComponent(volumeslider, () -> volumeslider.setBounds(getWidth() / 2 + 16,260,140,30));

        // The button for opening a lobby overview
        MBButton back = new MBButton("Back");
        back.addActionListener(e -> MB.show(last, true));
        addComponent(back, () -> back.setBounds(getWidth() / 2 - 70, 320, 140, 30));
        addKeybinding(
                false,
                "Close Settings",
                (e) -> MB.show(last, true),
                KeyEvent.VK_ESCAPE
        );

        // Add the buttons to a group
        addButtonGroup(fullscreen, antiAliasing, mute, back);
    }

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        setupButtonGroup();
    }

    /**
     * Change the screen state (window mode or fullscreen)
     */
    public void changeScreen() {
        // Change the state
        MB.settings.fullscreen = !MB.settings.fullscreen;

        // Get the active window
        Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();

        // Leave or enter fullscreen
        fullscreen.setText(MB.settings.fullscreen ? "Fullscreen" : "Window");
        if (MB.settings.fullscreen) {
            activeWindow.getGraphicsConfiguration().getDevice().setFullScreenWindow(MB.frame);
        } else {
            activeWindow.getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
        }
        // Save the settings
        MB.settings.saveSettings();
    }

    /**
     * Activate anti aliasing
     */
    public void changeAntiAliasing() {
        // Change the state
        MB.settings.antiAliasing = !MB.settings.antiAliasing;
        antiAliasing.setText(MB.settings.antiAliasing ? "Activated" : "Deactivated");
        MB.frame.repaint();

        // Save the settings
        MB.settings.saveSettings();
    }

    /**
     * Change the refresh rate
     */
    public void changeRefreshRate() {
        // Change the refresh rate
        switch (MB.settings.refreshRate) {
            case 60:
                MB.settings.refreshRate = 144;
                break;

            case 144:
                MB.settings.refreshRate = 244;
                break;

            case 244:
                MB.settings.refreshRate = 60;
                break;
        }
        // Update the wait value
        Game.WAIT_TIME = 1000 / MB.settings.refreshRate;

        // Change the button text
        refreshRate.setText(Integer.toString(MB.settings.refreshRate));

        // Save the settings
        MB.settings.saveSettings();
    }
}
