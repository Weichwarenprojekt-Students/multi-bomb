package Editor.Dialogs;

import Editor.Editor;
import Editor.MapManager;
import Game.Lobby;
import General.MB;
import General.Shared.MBButton;
import General.Shared.MBInput;
import General.Shared.MBLabel;

import javax.swing.*;
import java.awt.*;

public class SaveAs extends JPanel {
    /**
     * The margin
     */
    private static final int MARGIN = 16;
    /**
     * Button measurements
     */
    private static final int BUTTON_WIDTH = 100, BUTTON_HEIGHT = 30;
    /**
     * The maximum name length for a map
     */
    private static final int MAX_LENGTH = 16;

    /**
     * Constructor
     */
    public SaveAs() {
        setLayout(null);
        setBackground(Color.white);
        setBounds(0, 0, 3 * MARGIN + 2 * BUTTON_WIDTH, 50 + 2 * BUTTON_HEIGHT + MARGIN);

        // The title
        MBLabel title = new MBLabel(MBLabel.H2, "Enter the name");
        title.setFontColor(Color.BLACK);
        title.setBounds(MARGIN, MARGIN, getWidth() - 2 * MARGIN, 20);
        add(title);

        // The input field
        MBInput input = new MBInput();
        input.setBounds(MARGIN, 46, getWidth() - 2 * MARGIN, BUTTON_HEIGHT);
        add(input);
        
        // The cancel button
        MBButton cancel = new MBButton("Cancel");
        cancel.setBounds(MARGIN, 84, BUTTON_WIDTH, BUTTON_HEIGHT);
        cancel.addActionListener(e -> MB.activePanel.closeDialog());
        add(cancel);

        // The confirm button
        MBButton confirm = new MBButton("Save");
        confirm.setBounds(2 * MARGIN + BUTTON_WIDTH, 84, BUTTON_WIDTH, BUTTON_HEIGHT);
        confirm.addActionListener(e -> {
            // Check if the name is acceptable
            if (input.getText() == null || input.getText().equals("")) {
                MB.activePanel.toastError("Give the map a name!");
                return;
            } else if (input.getText().length() > MAX_LENGTH) {
                MB.activePanel.toastError("This name exceeds the", "character limit of " + MAX_LENGTH);
                return;
            } else if (MapManager.maps.containsKey(input.getText())) {
                MB.activePanel.toastError("This name is taken!");
                return;
            }
            MapManager.saveMapAs(Editor.map, input.getText());
            MB.activePanel.closeDialog();
        });
        add(confirm);
    }
}
