package Game.GameModes;

import Game.Models.Field;

public class BattleRoyale extends GameMode {
    /**
     * The description of the mode
     */
    public static final String DESCRIPTION = "The last man standing will be the winner!";

    /**
     * Constructor
     */
    public BattleRoyale() {
        super(GameMode.BATTLE_ROYALE, DESCRIPTION, Field.getAllItems());
    }

    @Override
    public void updateClientState() {

    }

    @Override
    public void updateServerState() {

    }
}
