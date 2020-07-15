package Editor;

import Game.Battleground;
import General.MB;
import General.Settings;
import General.Shared.MBInputDialog;
import General.Shared.MBPanel;
import Server.Messages.Socket.Map;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

public class MapManager {
    /**
     * The path to the custom maps
     */
    private final static String PATH = Settings.PATH + File.separator + "Maps";
    /**
     * The maps
     */
    public static HashMap<String, Map> maps = new HashMap<>();

    /**
     * Load all the maps
     */
    public static void loadMaps() {
        // Load the custom maps
        loadAllCustomMaps(new File(PATH));
        // Load the standard maps
        loadStandardMap("X-Factor");
        loadStandardMap("Labyrinth");
        loadStandardMap("Breakable");
        loadStandardMap("Cage-Fight");
        loadStandardMap("Krais");
        loadStandardMap("Mini Cage-Fight");
    }

    /**
     * Load all maps in a folder
     *
     * @param folder to be loaded
     */
    private static void loadAllCustomMaps(File folder) {
        if (folder.listFiles() != null) {
            Gson gson = new Gson();
            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                Map map;
                try {
                    map = gson.fromJson(Files.readString(fileEntry.toPath()), Map.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                maps.put(map.name, map);
            }
        }
    }

    /**
     * Load a standard map by its name
     *
     * @param name of the standard map
     */
    private static void loadStandardMap(String name) {
        Gson gson = new Gson();

        // Create the input stream
        InputStreamReader reader = new InputStreamReader(
                MapManager.class.getResourceAsStream("/Resources/StandardMaps/map_" + name + ".json")
        );

        // Add the map
        Map map = gson.fromJson(reader, Map.class);
        if (map != null) {
            maps.put(map.name, map);
        }
    }

    /**
     * Save a map
     *
     * @param map     the map to be saved
     * @param onClose event for the dialog
     */
    public static void saveMap(Map map, MBPanel.MBDialogManager.OnClose onClose) {
        if (maps.containsKey(map.name)) {
            if (maps.get(map.name).isCustom()) {
                save(map);
            } else {
                MB.activePanel.toastError("You cannot change", "a standard map!");
            }
        } else {
            MB.activePanel.showDialog(new MBInputDialog("Enter the map name", Editor.map.name, (text) -> {
                // Check if the name is acceptable
                if (MapManager.maps.containsKey(text)) {
                    MB.activePanel.toastError("This name is taken!");
                    return;
                }
                MapManager.saveMapAs(Editor.map, text);
                MB.activePanel.closeDialog();
            }), onClose);
        }
    }

    /**
     * Save a map with a new name
     *
     * @param map  the map to be saved
     * @param name the name of the map
     */
    public static void saveMapAs(Map map, String name) {
        // Copy the map and add it
        Map newMap = Map.copy(map);
        newMap.name = name;
        save(newMap);
    }

    /**
     * Save a map
     *
     * @param map to be save
     */
    private static void save(Map map) {
        // Check if the map contains 8 spawn points
        if (!map.allSpawnsSet()) {
            MB.activePanel.toastError("The map only has " + map.countSpawns() + " of 8 spawns!");
            return;
        }

        try {
            // Save the map
            FileWriter file = new FileWriter(PATH + File.separator + "map_" + map.name + ".json");
            file.write(map.toJson());
            file.close();

            // Update the map
            maps.put(map.name, map);
            Editor.map = map;
            Battleground.map = map;

            // Notify the user
            MB.activePanel.toastSuccess("Map was saved successfully!");
        } catch (IOException e) {
            MB.activePanel.toastError("Something went wrong!", "Map was not saved!");
        }
    }

    /**
     * Delete a map
     *
     * @param name of the map
     * @return true if the deletion was successful
     */
    public static boolean delete(String name) {
        File file = new File(PATH + File.separator + "map_" + name + ".json");
        if (file.delete()) {
            maps.remove(name);
            return true;
        }
        return false;
    }
}
