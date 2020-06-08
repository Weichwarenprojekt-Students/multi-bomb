package Menu.Dialogs;

import General.Shared.MBButton;
import General.Shared.MBLabel;
import Menu.Models.Lobby;

import javax.swing.*;

public class MapSelection extends JPanel {

    /**
     * The lobby to be modified
     */
    private final Lobby lobby;
    /**
     * The button to be modified
     */
    private final MBButton button;

    /**
     * Constructor
     */
    public MapSelection(Lobby lobby, MBButton button) {
        this.lobby = lobby;
        this.button = button;
        setLayout(null);
        setBounds(0, 0, 200, 200);

        // The title
        MBLabel title = new MBLabel("Choose a map", SwingConstants.CENTER, MBLabel.H2);
        title.setBounds(0, 20, getWidth(), 20);
        add(title);
    }
}
