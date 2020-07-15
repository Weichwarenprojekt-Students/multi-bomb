package Menu.Dialogs;

import Game.Lobby;
import General.MB;
import General.Shared.MBButton;
import General.Shared.MBLabel;

import javax.swing.*;
import java.awt.*;

public class HostPromotion extends JPanel {
    /**
     * The margin
     */
    public static final int MARGIN = 16;
    /**
     * Button measurements
     */
    public static final int BUTTON_WIDTH = 100, BUTTON_HEIGHT = 35;

    /**
     * Constructor
     */
    public HostPromotion(String player) {
        setLayout(null);
        setBackground(Color.white);
        setBounds(0, 0, 3 * MARGIN + 2 * BUTTON_WIDTH, 50 + BUTTON_HEIGHT + MARGIN);

        // The title
        MBLabel title = new MBLabel(MBLabel.H2, "Make " + player + " host?");
        title.setFontColor(Color.BLACK);
        title.setBounds(MARGIN, MARGIN, getWidth() - 2 * MARGIN, 20);
        add(title);

        // The cancel button
        MBButton cancel = new MBButton("Cancel");
        cancel.setBounds(MARGIN, 50, BUTTON_WIDTH, BUTTON_HEIGHT);
        cancel.addActionListener(e -> MB.activePanel.closeDialog());
        add(cancel);

        // The confirm button
        MBButton confirm = new MBButton("Confirm");
        confirm.setBounds(2 * MARGIN + BUTTON_WIDTH, 50, BUTTON_WIDTH, BUTTON_HEIGHT);
        confirm.addActionListener(e -> {
            Lobby.promoteHost(player);
            MB.activePanel.closeDialog();
        });
        add(confirm);
    }
}
