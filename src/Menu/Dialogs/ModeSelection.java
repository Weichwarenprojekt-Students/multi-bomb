package Menu.Dialogs;

import Game.GameModes.GameMode;
import General.MB;
import General.Shared.MBBackground;
import General.Shared.MBLabel;
import General.Shared.MBListView;
import Game.Lobby;

import javax.swing.*;
import java.awt.*;

public class ModeSelection extends JPanel {

    /**
     * Constructor
     */
    public ModeSelection() {
        setLayout(null);
        setBounds(0, 0, 400, 300);

        // The title
        MBLabel title = new MBLabel("Choose a mode", SwingConstants.CENTER, MBLabel.H2);
        title.setFontColor(Color.BLACK);
        title.setBounds(0, 20, getWidth(), 20);
        add(title);

        // The list
        MBListView<ModeItem> list = new MBListView<>();
        list.setBounds(4, 50, getWidth() - 8, 0);
        for (GameMode mode : GameMode.getModes()) {
            list.addItem(new ModeItem(mode));
        }
        add(list);

        // Resize the dialog
        setSize(getWidth(), list.getY() + list.getHeight() + 8);
    }

    private static class ModeItem extends MBListView.Item {
        /**
         * Label to display the name of the mode
         */
        private final MBLabel nameLabel;
        /**
         * Label to display the description of the mode
         */
        private final MBLabel descriptionLabel;
        /**
         * The mode
         */
        private final GameMode mode;
        /**
         * Constructor
         */
        public ModeItem(GameMode mode) {
            super(mode.name);
            this.mode = mode;

            // Add the components
            nameLabel = new MBLabel(MBLabel.NORMAL, name);
            nameLabel.setFontColor(Color.BLACK);
            descriptionLabel = new MBLabel(MBLabel.DESCRIPTION, mode.description);
            descriptionLabel.setFontColor(Color.BLACK);
            add(nameLabel);
            add(descriptionLabel);
        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, 60);
            nameLabel.setBounds(16, 10, width - 32, 20);
            descriptionLabel.setBounds(16, 30, width - 32, 20);
        }

        @Override
        public void onSelected() {
            Lobby.changeMode(mode);
            MB.activePanel.closeDialog();
        }

        /**
         * Paint the border
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            MB.settings.enableAntiAliasing(g);
            g.drawRoundRect(
                    4,
                    4,
                    getWidth() - 8,
                    getHeight() - 8,
                    MBBackground.CORNER_RADIUS,
                    MBBackground.CORNER_RADIUS
            );
        }
    }
}
