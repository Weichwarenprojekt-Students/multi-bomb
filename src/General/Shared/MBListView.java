package General.Shared;

import Game.Models.Player;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Comparator;

public class MBListView<T extends MBListView.Item> extends JPanel {
    /**
     * The list that contains the content
     */
    private final ArrayList<T> items = new ArrayList<>();
    /**
     * The comparator that describes how the list view should be sorted
     */
    private final Comparator<T> comparator;
    /**
     * True if the list shall be sorted
     */
    public boolean sort = true;
    /**
     * The last width of the view
     */
    private int lastWidth = 0;

    /**
     * Constructor
     */
    public MBListView() {
        this.comparator = Comparator.comparing(item -> item.name.toLowerCase());

        // Initialize the view
        setLayout(null);
        setOpaque(false);

        // Listen for resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (lastWidth != getWidth()) {
                    resizeList();
                }
                lastWidth = getWidth();
            }
        });
    }

    /**
     * Resize the lists items
     */
    private void resizeList() {
        int height = 0;
        for (Item item : items) {
            item.onResize(height, getWidth());
            height += item.getHeight();
        }
        setSize(getWidth(), height);
    }

    /**
     * Add an item
     *
     * @param item to be added
     */
    public void addItem(T item) {
        // Add the item and sort the list
        items.add(item);
        if (sort) {
            items.sort(comparator);
        }
        add(item);

        // Add a mouse listener to catch selections
        item.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                item.onSelected(items.indexOf(item));
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

        // Rebuild the list
        resizeList();
    }

    /**
     * Remove item from the list
     *
     * @param name of the item
     */
    public void removeItem(String name) {
        // Find the item
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).name.equals(name)) {
                index = i;
            }
        }

        // Check if the item was found
        if (index == -1) {
            return;
        }

        // Remove the item
        remove(items.get(index));
        items.remove(index);

        // Rebuild the list
        resizeList();
    }

    /**
     * Check if an item is already in the list
     *
     * @param name of the item
     */
    public boolean containsItem(String name) {
        for (T item : items) {
            if (item.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add Items that are in list but not in items list
     */
    public void addMissingItems(ArrayList<T> list) {
        // Check if an item is new
        for (T item : list) {
            if (!containsItem(item.name)) {
                addItem(item);
            }
        }

        // Check if an item was removed
        for (T item : items) {
            // Remove the item if it can't be found anymore
            boolean found = false;
            for (T newItem : list) {
                if (item.name.equals(newItem.name)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                removeItem(item.name);
            }
        }

        //Rebuild the list
        resizeList();
    }


    /**
     * The base class for an item
     */
    public static abstract class Item extends JLabel {
        /**
         * The name of the item
         */
        public String name;

        /**
         * Constructor
         */
        public Item(String name) {
            this.name = name;
        }

        /**
         * Handle resize events
         *
         * @param y     position
         * @param width of the list view
         */
        public abstract void onResize(int y, int width);


        /**
         * Handle selection of an item
         *
         * @param index of the selected item
         */
        public abstract void onSelected(int index);
    }
}
