package General.Shared;

import javax.swing.*;
import java.awt.*;

public class MBLabel extends JLabel {

    /**
     * Name of the global font
     */
    public static final String FONT_NAME = "Calibri";
    /**
     * Fontsize for H1 text
     */
    public static final int H1 = 20;
    /**
     * Fontsize for H2 text
     */
    public static final int H2 = 18;
    /**
     * Fontsize for normal text
     */
    public static final int NORMAL = 14;

    /**
     * Constructor
     *
     * @param text of the label
     */
    public MBLabel(String text) {
        super(text);
        setupLabel(NORMAL);
    }

    /**
     * Constructor
     *
     * @param fontSize of the text
     * @param text     of the label
     */
    public MBLabel(int fontSize, String text) {
        super(text);
        setupLabel(fontSize);
    }


    /**
     * Constructor
     *
     * @param text                of the label
     * @param horizontalAlignment of the text
     */
    public MBLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        setupLabel(NORMAL);
    }

    /**
     * Constructor
     *
     * @param text                of the label
     * @param horizontalAlignment of the text
     * @param fontSize            of the text
     */
    public MBLabel(String text, int horizontalAlignment, int fontSize) {
        super(text, horizontalAlignment);
        setupLabel(fontSize);
    }

    /**
     * Method to setup the label
     *
     * @param fontSize of the text
     */
    private void setupLabel(int fontSize) {
        setFont(new Font(FONT_NAME, Font.PLAIN, fontSize));
    }

    /**
     * Change the font color of the label
     *
     * @param color of the font
     */
    public void setFontColor(Color color) {
        setForeground(color);
    }

    /**
     * Set the font bold
     */
    public void setBold() {
        setFont(new Font(FONT_NAME, Font.BOLD, getFont().getSize()));
    }

    /**
     * Align the text to the top
     */
    public void alignTextTop() {
        setVerticalAlignment(JLabel.TOP);
        setVerticalTextPosition(JLabel.TOP);
    }
}
