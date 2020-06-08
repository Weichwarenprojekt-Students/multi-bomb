package Game.GameModes;

public class Classic extends GameMode {
    /**
     * The description of the mode
     */
    public static final String DESCRIPTION = "No flamethrower, no other items, no bullshit. Bombs only!";

    /**
     * Constructor
     */
    public Classic() {
        super(GameMode.CLASSIC, DESCRIPTION);
    }
}
