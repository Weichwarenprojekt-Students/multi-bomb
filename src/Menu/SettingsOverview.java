package Menu;

import Game.Game;
import General.MB;
import General.Shared.*;
import General.Sound.SoundControl;

import java.awt.*;

import static Menu.Menu.MARGIN;
import static Menu.Menu.START_Y;

/**
 * This class provides some settings like sound and graphics
 */
public class SettingsOverview extends MBPanel {
    /**
     * The last panel that was open
     */
    private final MBPanel last;
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

    /**
     * Constructor
     *
     * @param last panel that was visible
     */
    public SettingsOverview(MBPanel last) {
        super(true);
        this.last = last;
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBTitle title = new MBTitle("Settings");
        addComponent(title, () -> title.setBounds(
                (getWidth() - title.getWidth()) / 2,
                MARGIN,
                title.getWidth(),
                title.getHeight())
        );

        // The label for window mode
        int width = 200, height = 30, margin = 16;
        MBLabel fullscreenLabel = new MBLabel("Window Mode:");
        addComponent(fullscreenLabel, () -> fullscreenLabel.setBounds(
                getWidth() / 2 - width - margin,
                START_Y,
                width,
                height
        ));

        // The button for leaving or entering fullscreen
        fullscreen = new MBButton(MB.settings.fullscreen ? "Fullscreen" : "Window");
        fullscreen.addActionListener(e -> changeScreen());
        addComponent(fullscreen, () -> fullscreen.setBounds(
                getWidth() / 2 + margin,
                START_Y,
                width,
                height
        ));

        // The label for anti aliasing
        MBLabel antiAliasingLabel = new MBLabel("Anti Aliasing:");
        addComponent(antiAliasingLabel, () -> antiAliasingLabel.setBounds(
                getWidth() / 2 - width - margin,
                START_Y + height + margin,
                width,
                height
        ));

        // The button for activating or deactivating anti aliasing
        antiAliasing = new MBButton(MB.settings.antiAliasing ? "Activated" : "Deactivated");
        antiAliasing.addActionListener(e -> changeAntiAliasing());
        addComponent(antiAliasing, () -> antiAliasing.setBounds(
                getWidth() / 2 + margin,
                START_Y + height + margin,
                width,
                height
        ));

        // The label for anti aliasing
        MBLabel refreshRateLabel = new MBLabel("Refresh Rate:");
        addComponent(refreshRateLabel, () -> refreshRateLabel.setBounds(
                getWidth() / 2 - width - margin,
                START_Y + 2 * (height + margin),
                width,
                height
        ));

        // The button for activating or deactivating anti aliasing
        refreshRate = new MBButton(Integer.toString(MB.settings.refreshRate));
        refreshRate.addActionListener(e -> changeRefreshRate());
        addComponent(refreshRate, () -> refreshRate.setBounds(
                getWidth() / 2 + margin,
                START_Y + 2 * (height + margin),
                width,
                height
        ));

        // The label for muting the audio
        MBLabel musicLabel = new MBLabel("Music:");
        addComponent(musicLabel, () -> musicLabel.setBounds(
                getWidth() / 2 - width - margin,
                START_Y + 3 * (height + margin),
                width,
                height
        ));

        // The slider to handle the game volume
        MBSlider musicSlider = new MBSlider(
                SoundControl.getVolumePercent(MB.settings.musicVolume),
                SoundControl::changeMusicVolume
        );
        addComponent(musicSlider, () -> musicSlider.setBounds(
                getWidth() / 2 + margin,
                START_Y + 3 * (height + margin),
                width,
                height
        ));

        // The label for muting the audio
        MBLabel volumeLabel = new MBLabel("Sound Effect:");
        addComponent(volumeLabel, () -> volumeLabel.setBounds(
                getWidth() / 2 - width - margin,
                START_Y + 4 * (height + margin),
                width,
                height
        ));

        // The slider to handle the game volume
        MBSlider volumeSlider = new MBSlider(
                SoundControl.getVolumePercent(MB.settings.soundVolume),
                SoundControl::changeSoundVolume
        );
        addComponent(volumeSlider, () -> volumeSlider.setBounds(
                getWidth() / 2 + margin,
                START_Y + 4 * (height + margin),
                width,
                height
        ));

        // The button for opening a lobby overview
        MBButton back = new MBButton("Back");
        back.addActionListener(e -> MB.show(last, true));
        addComponent(back, () -> back.setBounds(
                (getWidth() - width) / 2,
                START_Y + 5 * (height + margin),
                width,
                height
        ));

        // Add the buttons to a group
        addButtonGroup(fullscreen, antiAliasing, back);
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

        // Give a warning if anti aliasing gets activated
        if (MB.settings.antiAliasing) {
            toastError("Anti Aliasing can cause", "serious performance issues!");
        }

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
