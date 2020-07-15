package Menu.Dialogs;

import General.MB;
import General.Shared.MBButton;
import General.Shared.MBLabel;

import javax.swing.*;
import java.awt.*;

public class HostGame extends JPanel {

    public HostGame() {
        setLayout(null);
        setBackground(Color.white);
        setBounds(0, 0, 3 * HostPromotion.MARGIN + 2 * HostPromotion.BUTTON_WIDTH, 50 + HostPromotion.BUTTON_HEIGHT + HostPromotion.MARGIN);

        //The title
        MBLabel title = new MBLabel(MBLabel.H2, "Host Server?");
        title.setBounds(HostPromotion.MARGIN, HostPromotion.MARGIN, getWidth() - 2 * HostPromotion.MARGIN, 20);
        add(title);

        // The cancel button
        MBButton cancel = new MBButton("Cancel");
        cancel.setBounds(HostPromotion.MARGIN, 50, HostPromotion.BUTTON_WIDTH, HostPromotion.BUTTON_HEIGHT);
        cancel.addActionListener(e -> MB.activePanel.closeDialog());
        add(cancel);

        // The confirm button
        MBButton confirm = new MBButton("Confirm");
        confirm.setBounds(2 * HostPromotion.MARGIN + HostPromotion.BUTTON_WIDTH, 50, HostPromotion.BUTTON_WIDTH, HostPromotion.BUTTON_HEIGHT);
        confirm.addActionListener(e -> {

        });
        add(confirm);
    }
}
