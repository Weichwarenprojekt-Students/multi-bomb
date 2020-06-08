package Game.GameModes;

public class BattleRoyale extends GameMode {
    /**
     * The description of the mode
     */
    public static final String DESCRIPTION = "The last man standing will be the winner!";

    /**
     * Constructor
     */
    public BattleRoyale() {
        super(GameMode.BATTLE_ROYALE, DESCRIPTION);
    }
}
