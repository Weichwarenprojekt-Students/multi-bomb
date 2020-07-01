package General.Shared;

import General.MB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public abstract class MBPanel extends JPanel {

    /**
     * The component sizer
     */
    private final ArrayList<ComponentResize> components = new ArrayList<>();
    /**
     * Other resize events
     */
    private final ArrayList<ComponentResize> events = new ArrayList<>();
    /**
     * The toast message manager
     */
    private final MBToastManager toastManager = new MBToastManager();
    /**
     * The dialog manager
     */
    private final MBDialogManager dialogManager = new MBDialogManager();
    /**
     * The buttons for the group
     */
    private MBButton[] buttons;
    /**
     * True if the panel shall contain the background
     */
    private final boolean background;

    /**
     * This class provides a general setup for a panel
     *
     * @param background true if
     */
    public MBPanel(boolean background) {
        // General stuff
        this.background = background;
        setLayout(null);
        setBackground(Color.white);

        // Add the components (first the toast component so it is always on top)
        addComponent(toastManager, () -> toastManager.setBounds(0, 0, getWidth(), getHeight()));
        addComponent(dialogManager, () -> dialogManager.setBounds(0, 0, getWidth(), getHeight()));

        // Listen for resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                synchronized (components) {
                    for (ComponentResize component : components) {
                        component.resize();
                    }
                    resize();
                }
            }
        });
    }

    /**
     * Toggle the resize events
     */
    public synchronized void resize() {
        for (ComponentResize resize : events) {
            resize.resize();
        }
    }

    /**
     * This method is executed when the panel is visible
     */
    public abstract void afterVisible();

    /**
     * Add a component and resize it
     */
    public void addComponent(JComponent component, ComponentResize componentSizer) {
        componentSizer.resize();
        components.add(componentSizer);
        add(component);
    }

    /**
     * Add a component resize event
     *
     * @param resize event to be added
     */
    public void addComponentEvent(ComponentResize resize) {
        components.add(resize);
    }


    /**
     * Add a resize event
     *
     * @param resize event to be added
     */
    public void addResizeEvent(ComponentResize resize) {
        events.add(resize);
    }

    /**
     * Add buttons to a group to activate arrow key navigation
     *
     * @param buttons to be added to a group
     */
    public void addButtonGroup(MBButton... buttons) {
        this.buttons = buttons;
    }

    /**
     * Activate the arrow key navigation for the button group
     */
    public void setupButtonGroup() {
        // Setup the listeners for the buttons
        for (int i = 0; i < buttons.length; i++) {
            int index = i;
            buttons[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        int nextIndex = buttons.length - 1 > index ? index + 1 : 0;
                        buttons[nextIndex].requestFocusInWindow();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        int nextIndex = index > 0 ? index - 1 : buttons.length - 1;
                        buttons[nextIndex].requestFocusInWindow();
                    }
                }
            });
        }

        // Set the focus on the first button (delay is required because instant execution leads to flickering)
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        buttons[0].requestFocus();
                    }
                },
                50
        );
    }

    /**
     * Add a key binding to the panel
     *
     * @param released true if the action should be executed on key release
     * @param key      name of the key
     * @param action   to be executed
     * @param keycodes to be bound to the action
     */
    public void addKeybinding(Boolean released, String key, ActionListener action, int... keycodes) {
        InputMap im = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        for (int code : keycodes) {
            im.put(KeyStroke.getKeyStroke(code, 0, released), key);
        }
        am.put(key, new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                action.actionPerformed(evt);
            }
        });
    }

    /**
     * Draw the background
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background) {
            if (MB.background != null) {
                g.drawImage(MB.background.image, getWidth() / 2 - MB.background.width / 2, 0, null);
            }
        }
    }

    /**
     * Make toast with green background
     *
     * @param text of the toast
     */
    public void toastSuccess(String... text) {
        new Thread(() -> toastManager.show(MBToastManager.GREEN, text)).start();
    }

    /**
     * Make toast with red background
     *
     * @param text of the toast
     */
    public void toastError(String... text) {
        new Thread(() -> toastManager.show(MBToastManager.RED, text)).start();
    }

    /**
     * Show a panel in a dialog
     *
     * @param panel to be shown
     * @param onClose the close handler
     */
    public void showDialog(JPanel panel, MBDialogManager.OnClose onClose) {
        this.dialogManager.show(panel, onClose);
    }

    /**
     * Close the current dialog
     */
    public void closeDialog() {
        this.dialogManager.close();
    }

    /**
     * Interface for resize events
     */
    public interface ComponentResize {
        void resize();
    }

    public static class MBToastManager extends JPanel {

        /**
         * The colors for the toasts
         */
        private static final Color GREEN = new Color(141, 211, 95), RED = new Color(255, 47, 0);
        /**
         * Duration of the toast
         */
        private static final int DURATION = 3000;
        /**
         * The margin of the toast
         */
        private static final int MARGIN = 16;
        /**
         * The padding of the toast
         */
        private static final int PADDING = 8;
        /**
         * The active toasts
         */
        private final ArrayList<JPanel> toasts = new ArrayList<>();

        /**
         * Constructor
         */
        public MBToastManager() {
            setLayout(null);
            setVisible(true);
            setOpaque(false);

            // Follow the frame
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    relocateToasts(false);
                }

                public void componentMoved(ComponentEvent e) {
                    relocateToasts(false);
                }

                public void componentShown(ComponentEvent e) {
                }

                public void componentHidden(ComponentEvent e) {
                }
            });
        }

        /**
         * Make the toast
         *
         * @param color of the toast
         * @param text  of the toast
         */
        private void show(Color color, String... text) {
            JPanel toast = new JPanel();
            toast.setLayout(null);
            toast.setVisible(true);
            toast.setOpaque(false);

            // Create the labels
            int width = 0, height = 0;
            for (String message : text) {
                MBLabel label = new MBLabel(message);
                label.setBold();
                label.alignTextTop();

                // Update the width
                int labelWidth = label.getFontMetrics(label.getFont()).stringWidth(message);
                label.setBounds(PADDING, PADDING + height, labelWidth, MBLabel.NORMAL + PADDING);
                width = Math.max(labelWidth, width);

                // Update height and add the label
                height += label.getHeight();
                toast.add(label);
            }
            toast.setSize(width + 2 * PADDING, height + PADDING);
            toast.add(new MBBackground(toast, color, false));

            // Relocate the toasts
            synchronized (this) {
                toasts.add(toast);
            }
            add(toast);
            relocateToasts(true);
            slide(toast, (progress, distance) -> (int) -(Math.sin(Math.PI / 2 * progress) * distance));

            // Show the toast
            try {
                Thread.sleep(DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            slide(toast, (progress, distance) -> (int) (progress * progress * distance));
            synchronized (this) {
                toasts.remove(toast);
            }
            remove(toast);
            revalidate();
            repaint();
        }

        /**
         * Let the toast slide in
         *
         * @param toast to be moved
         * @param fade  describes the fade progress
         */
        public static void slide(JComponent toast, FadeEvent fade) {
            // Initialize the animation
            final int duration = 500;
            final int delay = 10;
            final int distance = toast.getWidth() + MARGIN;
            final int startX = toast.getX();

            // Execute the fading
            for (int time = 0; time <= duration; time += delay) {
                // Move the toast
                toast.setLocation(startX + fade.fade((float) time / duration, distance), toast.getY());
                MB.activePanel.revalidate();
                MB.activePanel.repaint();

                // Wait
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            toast.setLocation(startX + fade.fade(1, distance), toast.getY());
            MB.activePanel.revalidate();
        }

        /**
         * Relocate the toast
         *
         * @param hideLatest true if first toast should be hidden
         */
        private synchronized void relocateToasts(boolean hideLatest) {
            // Move every toast
            int height = 0;
            for (int i = toasts.size() - 1; i >= 0; i--) {
                toasts.get(i).setLocation(
                        getWidth() - toasts.get(i).getWidth() - MBToastManager.MARGIN,
                        height + MARGIN
                );
                height += toasts.get(i).getHeight() + MARGIN / 2;
            }
            // Hide the newest toast
            if (hideLatest) {
                toasts.get(toasts.size() - 1).setLocation(getWidth(), toasts.get(toasts.size() - 1).getY());
            }
            revalidate();
            repaint();
        }

        /**
         * Interface that describes the fade behavior
         */
        public interface FadeEvent {
            int fade(float progress, int distance);
        }
    }

    public static class MBDialogManager extends JPanel {

        /**
         * The content of the dialog
         */
        private JPanel content;
        /**
         * On close handler for the dialog
         */
        private OnClose onClose;

        public MBDialogManager() {
            setLayout(null);
            setVisible(false);
            setOpaque(false);
        }

        /**
         * Show the dialog
         *
         * @param content to be shown
         * @param onClose the close handler
         */
        public void show(JPanel content, OnClose onClose) {
            // Overwrite the old content and remove it from the panel
            content.setOpaque(false);
            this.content = content;
            this.onClose = onClose;
            removeAll();
            revalidate();
            repaint();

            // Add the new contents and show the dialog
            centerContent();
            add(content);
            add(new MBBackground(content, Color.WHITE, true));
            setVisible(true);

            // Add resize listener
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    centerContent();
                }
            });
            content.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    centerContent();
                }
            });

            // Close the dialog on click
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    // Check whether the mouse is outside the dialog
                    boolean outsideX = e.getX() < content.getX() || e.getX() > content.getX() + content.getWidth();
                    boolean outsideY = e.getY() < content.getY() || e.getY() > content.getY() + content.getHeight();
                    // Close the dialog
                    if (outsideX || outsideY) {
                        close();
                    }
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
        }

        /**
         * Center the content panel
         */
        private void centerContent() {
            content.setBounds(
                    getWidth() / 2 - content.getWidth() / 2,
                    getHeight() / 2 - content.getHeight() / 2,
                    content.getWidth(),
                    content.getHeight()
            );
        }

        /**
         * Close the dialog
         */
        public void close() {
            setVisible(false);
            onClose.onClose();
        }

        /**
         * Paint the dialog
         *
         * @param g graphics
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            MB.settings.enableAntiAliasing(g);

            // Draw the background
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }

        /**
         * Interface to notify if the dialog is closed
         */
        public interface OnClose {
            void onClose();
        }
    }
}
