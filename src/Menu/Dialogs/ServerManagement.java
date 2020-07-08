package Menu.Dialogs;

import General.MB;
import General.Shared.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ServerManagement extends JPanel {

    /**
     * The measurements of the dialog
     */
    private static final int WIDTH = 800, HEIGHT = 500;
    /**
     * The measurements of the buttons
     */
    private static final int BUTTON_WIDTH = 200, BUTTON_HEIGHT = 40, BUTTON_PADDING = 8;
    /**
     * The outer margin
     */
    private static final int MARGIN = 16;
    /**
     * Selected address
     */
    private String selectedServer;

    /**
     * Dialog for managing the remote servers
     */
    public ServerManagement() {
        setLayout(null);
        setBackground(Color.white);
        setBounds(0, 0, WIDTH, HEIGHT);

        // Scrollable listView
        int padding = 4;
        MBListView<RemoteServerItem> listView = new MBListView<>();
        MBScrollView scroll = new MBScrollView(listView);
        scroll.setBounds(
                MARGIN + padding,
                MARGIN + padding,
                WIDTH - 4 * MARGIN - BUTTON_WIDTH - 2 * padding,
                HEIGHT - 2 * MARGIN - 2 * padding
        );
        add(scroll);
        for (String server : MB.settings.remoteServers) {
            listView.addItem(new RemoteServerItem(server));
        }

        // The background
        MBBackground background = new MBBackground(new Color(0, 0, 0, 0.2f));
        background.setBounds(MARGIN, MARGIN, WIDTH - 4 * MARGIN - BUTTON_WIDTH, HEIGHT - 2 * MARGIN);
        add(background);

        // The input field
        MBInput input = new MBInput();
        input.setText("New Address");
        input.setBounds(WIDTH - BUTTON_WIDTH - MARGIN, MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
        add(input);

        // The add button
        MBButton add = new MBButton("Add");
        add.setBounds(
                WIDTH - BUTTON_WIDTH - MARGIN,
                BUTTON_HEIGHT + MARGIN + BUTTON_PADDING,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        );
        add.addActionListener(e -> {
            if (input.getText().isEmpty()) {
                MB.activePanel.toastError("Specify an ip address first!");
            } else if (isLocal(input.getText())) {
                MB.activePanel.toastError("This is a local server!", "Local servers are shown automatically!");
            } else if (MB.settings.remoteServers.contains(input.getText())) {
                MB.activePanel.toastError("This server is already added!");
            } else {
                // Validate the address
                try {
                    // If this doesn't fail the address is valid
                    new URL("http://" + input.getText()).toURI();

                    // Add the address
                    listView.addItem(new RemoteServerItem(input.getText()));
                    MB.settings.remoteServers.add(input.getText());
                    MB.settings.saveSettings();
                } catch (Exception exception) {
                    MB.activePanel.toastError("Address not valid!");
                }
            }
        });
        add(add);

        // The delete button
        MBButton delete = new MBButton("Delete");
        delete.setBounds(
                WIDTH - BUTTON_WIDTH - MARGIN,
                2 * BUTTON_HEIGHT + MARGIN + 2 * BUTTON_PADDING,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        );
        delete.addActionListener(e -> {
            if (selectedServer == null || !MB.settings.remoteServers.contains(selectedServer)) {
                MB.activePanel.toastError("Select a server first!");
            } else {
                listView.removeItem(selectedServer);
                MB.settings.remoteServers.remove(selectedServer);
                MB.settings.saveSettings();
            }
        });
        add(delete);

        // The close button
        MBButton close = new MBButton("Close");
        close.setBounds(
                WIDTH - BUTTON_WIDTH - MARGIN,
                HEIGHT - BUTTON_HEIGHT - MARGIN,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        );
        close.addActionListener(e -> MB.activePanel.closeDialog());
        add(close);
    }

    /**
     * Check whether the address is local
     *
     * @param address to be added
     * @return true if the address is local
     */
    private boolean isLocal(String address) {
        return  address.startsWith("192.168") || address.equals("localhost") || address.equals("127.0.0.1")
                || address.startsWith("10.") || address.equals("0.0.0.0");
    }

    public class RemoteServerItem extends MBListView.Item {

        /**
         * Label for name
         */
        private final MBLabel nameLabel;

        /**
         * Constructor
         *
         * @param name of the server
         */
        public RemoteServerItem(String name) {
            super(name);
            setLayout(null);

            // Add the labels
            nameLabel = new MBLabel(name);
            nameLabel.setBold();
            add(nameLabel);
        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, 30);
            nameLabel.setBounds(12, 5, width - 32, 20);
        }

        @Override
        public void onSelected() {
            selectedServer = name;
            MB.frame.revalidate();
            MB.frame.repaint();
        }

        /**
         * Paint the border
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            MB.settings.enableAntiAliasing(g);
            if (selectedServer != null && selectedServer.equals(this.name)) {
                g.setColor(Color.WHITE);
                g.drawRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        MBBackground.CORNER_RADIUS,
                        MBBackground.CORNER_RADIUS
                );
            }
        }
    }
}
