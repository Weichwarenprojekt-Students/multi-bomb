package Menu.Dialogs;

import General.MB;
import General.Shared.MBButton;
import General.Shared.MBInput;
import General.Shared.MBLabel;
import Menu.LobbyView;

import javax.swing.*;
import java.awt.*;


import static Menu.Dialogs.HostPromotion.MARGIN;
import static Menu.Dialogs.HostPromotion.BUTTON_HEIGHT;
import static Menu.Dialogs.HostPromotion.BUTTON_WIDTH;

public class EnterPlayerName extends JPanel {
    public EnterPlayerName(LobbyView lobbyView) {
        setLayout(null);
        setBackground(Color.white);
        setBounds(0, 0, 3 * MARGIN + 2 * BUTTON_WIDTH, 100 + BUTTON_HEIGHT + MARGIN);

        //The title
        MBLabel title = new MBLabel(MBLabel.H2, "Enter player name");
        title.setBounds(MARGIN, MARGIN, getWidth() - 2 * MARGIN, 20);
        add(title);

        //Input field
        MBInput input = new MBInput();
        input.setBounds(MARGIN, 50, BUTTON_WIDTH, BUTTON_HEIGHT);
        add(input);

        //The cancel button
        MBButton cancel = new MBButton("Cancel");
        cancel.setBounds(MARGIN, 100, BUTTON_WIDTH, BUTTON_HEIGHT);
        cancel.addActionListener(e -> MB.activePanel.closeDialog());
        add(cancel);

        //The confirm button
        MBButton confirm = new MBButton("Confirm");
        confirm.setBounds(2 * MARGIN + BUTTON_WIDTH, 100, BUTTON_WIDTH, BUTTON_HEIGHT);
        confirm.addActionListener(e -> {
            lobbyView.setPlayerName(input.getText());
            MB.activePanel.closeDialog();
        });
        add(confirm);
    }
}
