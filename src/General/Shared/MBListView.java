package General.Shared;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class MBListView<T extends MBListView.Item> extends JPanel {
    /**
     * The list that contains the content
     */
    private final TreeMap<String, T> items = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    /**
     * The order can be used to give the list a specific order
     */
    public ArrayList<String> order;
    /**
     * The last width of the view
     */
    private int lastWidth = 0;

    /**
     * Constructor
     */
    public MBListView() {
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
     * Give the list a specific order
     */
    public void setSpecificOrder(String... order) {
        this.order = new ArrayList<>(Arrays.asList(order));
    }

    /**
     * Resize and reposition the lists items
     */
    private void resizeList() {
        int height = 0;
        if (order != null) {
            for (String name : order) {
                items.get(name).onResize(height, getWidth());
                height += items.get(name).getHeight();
            }
        } else {
            for (Item item : items.values()) {
                item.onResize(height, getWidth());
                height += item.getHeight();
            }
        }
        setSize(getWidth(), height);
    }

    /**
     * Add an item
     *
     * @param item to be added
     */
    public void addItem(T item) {
        // Check if item has to be removed
        removeItem(item.name);

        // Add the item
        items.put(item.name, item);
        add(item);

        // Add a mouse listener to catch selections
        item.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                item.onSelected();
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
     * Check if the list contains an item
     *
     * @param name of the item
     * @return true if the item is in the list
     */
    public boolean containsItem(String name) {
        return items.containsKey(name);
    }

    /**
     * Get an item
     *
     * @param name of the item
     * @return the corresponding item
     */
    public T getItem(String name) {
        return items.get(name);
    }

    /**
     * Remove item from the list
     *
     * @param name of the item
     */
    public void removeItem(String name) {
        // Check if the list contains the item
        if (!items.containsKey(name)) {
            return;
        }

        // Remove the item
        remove(items.get(name));
        items.remove(name);

        // Rebuild the list
        resizeList();
    }

    /**
     * Add Items that are in list but not in items list
     */
    public void addMissingItems(Set<String> list, AddMissingItems<T> event) {
        // Check if an item is new
        for (String item : list) {
            if (!items.containsKey(item)) {
                addItem(event.newItem(item));
            }
        }

        // Check if an item was removed
        ArrayList<String> removedItems = new ArrayList<>();
        for (Map.Entry<String, T> item : items.entrySet()) {
            if (!list.contains(item.getKey())) {
                removedItems.add(item.getKey());
            }
        }
        for (String name : removedItems) {
            removeItem(name);
        }

        // Rebuild the list
        resizeList();
    }

    public interface AddMissingItems<T> {
        T newItem(String name);
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
         */
        public abstract void onSelected();
    }
}
