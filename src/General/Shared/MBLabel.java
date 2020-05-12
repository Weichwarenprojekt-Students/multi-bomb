package General.Shared;

import javax.swing.*;
import java.awt.*;

public class MBLabel extends JLabel {

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
        setFont(new Font("Calibri", Font.PLAIN, fontSize));
    }
}
