package Menu.Dialogs;

import Editor.MapManager;
import Game.Battleground;
import Game.Lobby;
import General.MB;
import General.Shared.MBButton;
import General.Shared.MBLabel;
import Server.Messages.Socket.Map;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MapSelection extends JPanel {

    /**
     * The index of the selected map
     */
    private static int selectedId = 0;
    /**
     * The maps in list form
     */
    ArrayList<Map> maps;

    /**
     * Constructor
     */
    public MapSelection(MBButton button) {
        setLayout(null);
        setBackground(Color.white);
        setBounds(0, 0, 400, 488);

        // Get the maps
        maps = new ArrayList<>(MapManager.maps.values());

        // The title
        MBLabel title = new MBLabel("Choose a map - " + maps.get(selectedId).name, SwingConstants.CENTER, MBLabel.H2);
        title.setFontColor(Color.BLACK);
        title.setBounds(0, 20, getWidth(), 20);
        add(title);

        // The battleground
        int margin = 16;
        Battleground battleground = new Battleground(maps.get(selectedId), false, true);
        battleground.setBounds(margin, 54, 400 - 2 * margin, 400 - 2 * margin);
        add(battleground);
        battleground.afterVisible();

        // The buttons
        int width = 100, height = 30;

        // The previous button
        MBButton previous = new MBButton("Previous");
        previous.setBounds(margin, 440, width, height);
        previous.addActionListener(e -> {
            if (selectedId <= 0) {
                selectedId = maps.size() - 1;
            } else {
                selectedId--;
            }
            title.setText("Choose a map - " + maps.get(selectedId).name);
            Battleground.map = maps.get(selectedId);
            battleground.repaint();
        });
        add(previous);

        // The confirm button
        MBButton confirm = new MBButton("Confirm");
        confirm.setBounds((getWidth() - width) / 2, 440, width, height);
        confirm.addActionListener(e -> {
            Lobby.map = maps.get(selectedId);
            MB.activePanel.closeDialog();
            button.setText("Map: " + maps.get(selectedId).name);
        });
        add(confirm);

        // The next button
        MBButton next = new MBButton("Next");
        next.setBounds(getWidth() - width - margin, 440, width, height);
        next.addActionListener(e -> {
            if (selectedId >= maps.size() - 1) {
                selectedId = 0;
            } else {
                selectedId++;
            }
            title.setText("Choose a map - " + maps.get(selectedId).name);
            Battleground.map = maps.get(selectedId);
            battleground.repaint();
        });
        add(next);
    }
}
