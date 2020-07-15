package Editor;

import Game.Battleground;
import Game.Models.Field;
import General.MB;
import General.MultiBomb;
import General.Shared.MBPanel;
import Server.Messages.Socket.Map;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static Game.Game.WAIT_TIME;

/**
 * This class is the core of the editor
 */
public class Editor extends MBPanel {

    /**
     * The margin of the sidebar
     */
    public static int MARGIN = 16;
    /**
     * True if the game is over
     */
    public static boolean editingFinished = true;
    /**
     * The id of the selected tool
     */
    public static byte selectedId = Field.SOLID_0.id;
    /**
     * The map to be edited
     */
    public static Map map;
    /**
     * The sidebar
     */
    private Sidebar sidebar;
    /**
     * The battleground
     */
    private Battleground battleground;

    /**
     * Constructor
     */
    public Editor(Map map) {
        super(true);
        Editor.map = map;
    }

    /**
     * Method that is executed when panel is visible
     */
    @Override
    public void afterVisible() {
        sidebar = new Sidebar();
        battleground = new Battleground(map, false, true);
        setupEditor();

        // Add the battleground
        addComponent(battleground, () -> {
            battleground.setBounds(
                    (int) (getWidth() / 2 - 0.25 * getHeight()) + 2 * MARGIN,
                    2 * MARGIN,
                    getHeight() - 4 * MARGIN,
                    getHeight() - 4 * MARGIN
            );
            battleground.calculateSize();
        });

        // Add the sidebar
        addComponent(sidebar, () -> sidebar.setBounds(
                (int) (getWidth() / 2 - 0.75 * getHeight()) + MARGIN,
                MARGIN,
                (int) (1.5 * getHeight()) - 2 * MARGIN,
                getHeight() - 2 * MARGIN
        ));

        // Call the after visible methods and start the drawing
        battleground.afterVisible();
        sidebar.afterVisible();
        new Thread(this::startDrawing).start();
    }

    /**
     * Setup the click listener on the battleground
     */
    private void setupEditor() {
        // Change field on mouse press
        battleground.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                changeField(e, true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // Change field on drag
        battleground.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                changeField(e, false);
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }

    /**
     * Try to set or remove a field
     *
     * @param e            the mouse event
     * @param notification true if the user shall be notified
     */
    private void changeField(MouseEvent e, boolean notification) {
        // Calculate the field position
        int m = (e.getY() - Battleground.offset) / Battleground.fieldSize;
        int n = (e.getX() - Battleground.offset) / Battleground.fieldSize;

        // Check if it is right click
        boolean right = SwingUtilities.isRightMouseButton(e);

        // Check if the mouse is in the field
        if (m > 0 && m < Map.SIZE - 1 && n > 0 && n < Map.SIZE - 1) {
            // Check if the field is a spawn point
            int spawn = -1;
            for (int i = 0; i < map.spawns.length; i++) {
                if (map.spawns[i] != null && map.spawns[i].x == n && map.spawns[i].y == m) {
                    spawn = i;
                    break;
                }
            }
            // Remove the spawn if there is one
            if (spawn > -1) {
                map.spawns[spawn] = null;
            }

            // Check if it was a right click
            if (right) {
                map.setField(m, n, Field.GROUND.id);
            } else {
                // Check if the user want's to place a spawn
                if (selectedId == Field.SPAWN.id) {
                    if (map.setSpawn(m, n)) {
                        map.setField(m, n, (byte) 0);
                    } else if (notification) {
                        MB.activePanel.toastError(
                                "You can't place more spawns!",
                                "There are already 8 on the map!"
                        );
                    }
                } else {
                    map.setField(m, n, selectedId);
                }
            }
        }
    }

    /**
     * Start the game
     */
    private void startDrawing() {
        // Start the draw loop
        editingFinished = false;
        MultiBomb.startTimedAction(WAIT_TIME, ((deltaTime, totalTime) -> {
            // Update the player and repaint
            MB.frame.revalidate();
            MB.frame.repaint();

            // Stop drawing if editing is finished
            return !editingFinished;
        }));
    }
}
