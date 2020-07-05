package Editor;

import General.MB;
import General.Shared.*;
import Menu.Menu;
import Server.Messages.Socket.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MapSelection extends MBPanel {

    /**
     * Distance between buttons and map list
     */
    private static final int MARGIN = 32;
    /**
     * Measurements of the buttons
     */
    private static final int BUTTON_WIDTH = 200, BUTTON_HEIGHT = 40;
    /**
     * The list showing the maps
     */
    private MBListView<MapItem> list;
    /**
     * The buttons for map configuration
     */
    private MBButton newMap, edit, delete, openLast;
    /**
     * The leave button
     */
    private MBButton leave;
    /**
     * The selected map
     */
    private Map selectedMap;
    /**
     * Constructor
     */
    public MapSelection() {
        super(true);
        setupLayout();
    }

    /**
     * Setup the layout
     */
    public void setupLayout() {
        // The title
        MBLabel title = new MBLabel("Map Selection", SwingConstants.CENTER, MBLabel.H1);
        addComponent(title, () -> title.setBounds(getWidth() / 2 - 150, 32, 300, 40));

        // The list view
        list = new MBListView<>();
        MBScrollView scroll = new MBScrollView(list);
        addComponent(scroll, () -> scroll.setBounds(
                getWidth() / 2 - (getHeight() + MARGIN + BUTTON_WIDTH) / 2,
                96,
                getHeight(),
                getHeight() - 160
        ));

        // The new button
        newMap = new MBButton("New");
        newMap.addActionListener(e -> MB.show(new Editor(new Map()), false));
        addComponent(newMap, () -> newMap.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() - 4,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));

        // The edit button
        edit = new MBButton("Edit");
        edit.enabled = false;
        edit.addActionListener(e -> {
            if (selectedMap == null) {
                toastError("Select a map first!");
            } else {
                MB.show(new Editor(Map.copy(selectedMap)), false);
            }
        });
        addComponent(edit, () -> edit.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 46,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));

        // The delete button
        delete = new MBButton("Delete");
        delete.enabled = false;
        delete.addActionListener(e -> {
            if (selectedMap == null) {
                toastError("Select a map first!");
            } else if (!selectedMap.isCustom()) {
                toastError("You cannot delete", "a standard map!");
            } else if (MapManager.delete(selectedMap.name)) {
                list.removeItem(selectedMap.name);
                toastSuccess("Map was deleted!");
            } else {
                toastError("Something went wrong!", "Map was not deleted!");
            }
         });
        addComponent(delete, () -> delete.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 96,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));

        // The open last button
        openLast = new MBButton("Open Last");
        openLast.setVisible(Editor.map != null);
        openLast.addActionListener(e -> MB.show(new Editor(Editor.map), false));
        addComponent(openLast, () -> openLast.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + 146,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));

        // The leave button
        leave = new MBButton("Leave");
        leave.addActionListener(e -> MB.show(new Menu(), false));
        addComponent(leave, () -> leave.setBounds(
                scroll.getX() + scroll.getWidth() + MARGIN,
                scroll.getY() + scroll.getHeight() - BUTTON_HEIGHT + 4,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        ));

        // The background
        MBBackground background = new MBBackground(new Color(0, 0, 0, 0.2f));
        addComponent(background, () -> background.setBounds(
                scroll.getX() - 4,
                scroll.getY() - 4,
                scroll.getWidth() + 8,
                scroll.getHeight() + 8
        ));
        
        addButtonGroup(newMap, edit, delete, leave);
    }

    @Override
    public void afterVisible() {
        setupButtonGroup();

        // Fill the list
        for (java.util.Map.Entry<String, Map> map : MapManager.maps.entrySet()) {
            list.addItem(new MapItem(map.getValue()));
        }

        // Add keybinding
        addKeybinding(
                false,
                "Close Map Selection",
                (e) -> MB.show(new Menu(), false),
                KeyEvent.VK_ESCAPE
        );
    }

    private class MapItem extends MBListView.Item {
        /**
         * The label showing the name
         */
        private final MBLabel nameLabel;
        /**
         * The label showing the description
         */
        private final MBLabel descriptionLabel;
        /**
         * The corresponding map item
         */
        private final Map map;

        /**
         * Constructor
         */
        public MapItem(Map map) {
            super(map.name);
            this.map = map;
            setLayout(null);

            // Add the labels
            nameLabel = new MBLabel(name);
            nameLabel.setBold();
            add(nameLabel);
            descriptionLabel = new MBLabel(MBLabel.DESCRIPTION, map.description);
            add(descriptionLabel);
        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, 50);
            nameLabel.setBounds(12, 6, width - 32, 20);
            descriptionLabel.setBounds(12, 26, width - 32, 20);
        }

        @Override
        public void onSelected(int index) {
            edit.enabled = true;
            delete.enabled = true;
            selectedMap = map;
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
            if (selectedMap != null && selectedMap.name.equals(map.name)) {
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
