package Game.GameModes;

import Game.Models.Field;

public class Classic extends GameMode {
    /**
     * The description of the mode
     */
    public static final String DESCRIPTION = "No flamethrower, no other items, no bullshit. Bombs only!";

    /**
     * Constructor
     */
    public Classic() {
        super(GameMode.CLASSIC, DESCRIPTION, Field.BOMB.id, Field.HEART.id, Field.EXPLOSION.id, Field.SPEED.id);
    }

    @Override
    public void updateClientState() {

    }

    @Override
    public void updateServerState() {

    }
}
