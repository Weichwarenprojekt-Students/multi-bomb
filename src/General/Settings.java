package General;

import com.google.gson.Gson;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Settings {

    /**
     * The general path to the game's main directory
     */
    public static String PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator
            + "My Games" + File.separator + "Multi Bomb" + File.separator;
    /**
     * The window measurements
     */
    public int x = 200, y = 200, width = 800, height = 500;
    /**
     * True if the window is in fullscreen
     */
    public boolean fullscreen = false;
    /**
     * True if anti aliasing is active
     */
    public boolean antiAliasing = true;
    /**
     * The refresh rate the game should target
     */
    public int refreshRate = 60;
    /**
     * Player name
     */
    public String playerName ="";
    /**
     * Remote server
     */
    public ArrayList<String> remoteServer = new ArrayList<>();

    /**
     * Load the settings
     */
    public void loadSettings() {
        try {
            Gson gson = new Gson();
            MB.settings = gson.fromJson(new FileReader(PATH + "Settings.json"), Settings.class);
        } catch (FileNotFoundException e) {
            // Create the file and make the directories if necessary
            File file = new File(PATH + "Maps");
            if (file.mkdirs()) {
                saveSettings();
            } else {
                System.out.println("ERROR! Couldn't setup the directory for the game data!");
            }
        }
    }

    /**
     * Save the settings
     */
    public void saveSettings() {
        try {
            Gson gson = new Gson();

            // Save to file
            String json = gson.toJson(this);
            BufferedWriter writer = new BufferedWriter(new FileWriter(PATH + "Settings.json"));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enable anti aliasing if setting is active
     *
     * @param g graphics
     */
    public void enableAntiAliasing(Graphics g) {
        if (antiAliasing) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        }
    }
}
