package Editor;

import Editor.Dialogs.SaveAs;
import Game.Models.Field;
import General.MB;
import General.Shared.*;

import java.awt.*;
import java.awt.event.KeyEvent;

import static General.Shared.MBLabel.FONT_NAME;
import static General.Shared.MBPanel.MBToastManager.slide;

public class Sidebar extends MBPanel {
    /**
     * The padding for the content of the sidebar
     */
    public static final int PADDING = 8;
    /**
     * The list view for the user hud
     */
    private final MBListView<ToolItem> list = new MBListView<>();
    /**
     * True if the menu is opened
     */
    private boolean menuOpen = false;
    /**
     * The buttons for the menu
     */
    private MBButton save, saveAs, leave;
    /**
     * The informational item
     */
    ToolItem erase;
    /**
     * The menu button
     */
    private MBImageButton menu;

    /**
     * Constructor
     */
    public Sidebar() {
        super(false);
        setOpaque(false);
        setBackground(null);
        setupLayout();
    }

    /**
     * Setup the layout
     */
    private void setupLayout() {
        // The name of the mode
        MBLabel title = new MBLabel(MBLabel.H2, Editor.map.name);
        addComponent(title, () -> title.setBounds(2 * PADDING + 32, PADDING, 200, 32));

        // Add the save button
        int startY = 2 * PADDING + 32, height = 40, margin = 16;
        save = new MBButton("Save");
        addComponent(save, () -> save.setBounds(
                menuOpen ? PADDING : -getHeight() / 2 - PADDING,
                startY,
                getHeight() / 2,
                height
        ));
        save.addActionListener((e) -> MapManager.saveMap(Editor.map, () -> title.setText(Editor.map.name)));

        // Add the save as button
        saveAs = new MBButton("Save As");
        addComponent(saveAs, () -> saveAs.setBounds(
                menuOpen ? PADDING : -getHeight() / 2 - PADDING,
                startY + height + margin,
                getHeight() / 2,
                height
        ));
        saveAs.addActionListener((e) -> MB.activePanel.showDialog(new SaveAs(), () -> title.setText(Editor.map.name)));

        // Add the leave button
        leave = new MBButton("Leave");
        addComponent(leave, () -> leave.setBounds(
                menuOpen ? PADDING : -getHeight() / 2 - PADDING,
                startY + 2 * (height + margin),
                getHeight() / 2,
                height
        ));
        leave.addActionListener((e) -> {
            Editor.editingFinished = true;
            MB.show(new MapSelection(), false);
        });

        // The list view
        MBScrollView scroll = new MBScrollView(list);
        addComponent(scroll, () -> scroll.setBounds(
                PADDING,
                startY,
                getHeight() / 2 + 16,
                getHeight() - startY - PADDING
        ));
    }

    @Override
    public void afterVisible() {
        // The pause button
        menu = new MBImageButton("General/menu.png");
        addComponent(menu, () -> menu.setBounds(PADDING, PADDING, 32, 32));
        menu.addActionListener(this::openMenu);

        // Add an informational part
        erase = new ToolItem(Field.GROUND, "Use right click for ground fields.", -11,"ground");
        addComponent(erase, () -> {
            erase.onResize(getHeight() - erase.getHeight() - PADDING, getHeight() / 2 + 16);
            erase.setBounds(
                    PADDING,
                    getHeight() - erase.getHeight() - PADDING,
                    getHeight() / 2 + 16,
                    erase.getHeight()
            );
        });

        // Add the background
        MBBackground background = new MBBackground(MBButton.BACKGROUND_COLOR);
        addComponent(background, () -> background.setBounds(0, 0, getWidth(), getHeight()));

        // Fill the list
        list.sort = false;
        list.addItem(new ToolItem(Field.SOLID_0, "An unbreakable tree.", -2, "solid_0"));
        list.addItem(new ToolItem(Field.SOLID_1, "An unbreakable fir.", 2,"solid_1"));
        list.addItem(new ToolItem(Field.BREAKABLE_0, "A breakable mushroom.", -4,"breakable_0"));
        list.addItem(new ToolItem(Field.BREAKABLE_1, "A breakable stack of wood.", -10,"breakable_1"));
        list.addItem(new ToolItem(Field.SPAWN, "The map requires 8 spawns.", -12,"spawn"));

        // Add keybinding for escape
        addKeybinding(
                false,
                "Open Menu",
                (e) -> {
                    if (menu.enabled) {
                        openMenu();
                    }
                },
                KeyEvent.VK_ESCAPE
        );
    }

    /**
     * Open the menu
     */
    private void openMenu() {
        // Disable the button and change the state
        menu.enabled = false;
        menuOpen = !menuOpen;

        // Show or hide the buttons
        int delay = 200;
        if (menuOpen) {
            list.setVisible(false);
            erase.setVisible(false);
            // Start moving the save button
            new Thread(() -> slide(save, (progress, distance) ->
                    menuOpen ? (int) (Math.sin(Math.PI / 2 * progress) * distance) : 0)).start();

            // Wait a bit and move the save as button
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                slide(saveAs, (progress, distance) ->
                        menuOpen ? (int) (Math.sin(Math.PI / 2 * progress) * distance) : 0);
            }).start();

            // Wait a bit and move the leave button
            new Thread(() -> {
                try {
                    Thread.sleep(2 * delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                slide(leave, (progress, distance) ->
                        menuOpen ? (int) (Math.sin(Math.PI / 2 * progress) * distance) : 0);

                // Ensure right position
                save.setLocation(PADDING, save.getY());
                saveAs.setLocation(PADDING, saveAs.getY());
                leave.setLocation(PADDING, leave.getY());
                menu.enabled = true;
            }).start();
        } else {
            // Hide the leave button
            new Thread(() -> slide(leave, (progress, distance)
                    -> menuOpen ? 0 : (int) -(progress * progress * distance))).start();

            // Wait a bit and hide the save as button
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                slide(saveAs, (progress, distance) -> menuOpen ? 0 : (int) -(progress * progress * distance));
            }).start();

            // Wait a bit and hide the save button
            new Thread(() -> {
                try {
                    Thread.sleep(2 * delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                slide(save, (progress, distance) -> menuOpen ? 0 : (int) -(progress * progress * distance));

                // Ensure right position
                save.setLocation(-getHeight() / 2 - PADDING, save.getY());
                saveAs.setLocation(-getHeight() / 2 - PADDING, saveAs.getY());
                leave.setLocation(-getHeight() / 2 - PADDING, leave.getY());
                list.setVisible(true);
                erase.setVisible(true);
                menu.enabled = true;
            }).start();
        }
    }

    private static class ToolItem extends MBListView.Item {
        /**
         * The font size for the information text
         */
        private final Font font = new Font(FONT_NAME, Font.PLAIN, MBLabel.DESCRIPTION);
        /**
         * The vertical offset for the logo
         */
        private final int offsetY;
        /**
         * The corresponding field
         */
        private final Field field;
        /**
         * The label showing the name
         */
        private final MBLabel nameLabel;
        /**
         * The description of the tool
         */
        private final String description;
        /**
         * The measurements of the item
         */
        private final int height = 64, padding = 10, textStart = 54;
        /**
         * The logo of the tool
         */
        private MBImage logo;

        /**
         * Constructor
         */
        public ToolItem(Field field, String description, int offsetY, String image) {
            super(field.name);
            this.field = field;
            this.description = description;
            this.offsetY = offsetY;
            this.logo = new MBImage("Maps/Forest/" + image + ".png", () -> {
                logo.width = 40;
                logo.height = 60;
            });
            setLayout(null);

            // Add the name
            nameLabel = new MBLabel(name);
            nameLabel.setBold();
            add(nameLabel);
        }

        @Override
        public void onResize(int y, int width) {
            setBounds(0, y, width, height);
            nameLabel.setBounds(textStart, padding, width - 32, 20);
        }

        /**
         * Select the right field
         */
        @Override
        public void onSelected(int index) {
            Editor.selectedId = field.id;
        }

        /**
         * Paint the border
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            MB.settings.enableAntiAliasing(g);

            // Draw the image
            g.drawImage(logo.image, 6, offsetY, null);

            // Draw the selection rectangle
            if (Editor.selectedId == field.id) {
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

            // Draw the description
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString(
                    description,
                    textStart,
                    height - padding - 6
            );
        }
    }
}
