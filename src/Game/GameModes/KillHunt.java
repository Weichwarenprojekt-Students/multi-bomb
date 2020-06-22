package Game.GameModes;

import Game.Models.Field;

public class KillHunt extends GameMode {
    /**
     * The description of the mode
     */
    public static final String DESCRIPTION = "The first one to get 10 kills is the winner!";

    /**
     * Constructor
     */
    public KillHunt() {
        super(GameMode.KILL_HUNT, DESCRIPTION, Field.getAllItems());
    }

    @Override
    public void updateClientState() {

    }

    @Override
    public void updateServerState() {

    }
}
