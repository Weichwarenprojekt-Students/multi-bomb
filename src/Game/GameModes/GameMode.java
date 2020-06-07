package Game.GameModes;

import java.util.ArrayList;
import java.util.Arrays;

public class GameMode {
    /**
     * The available modes
     */
    public static final String BATTLE_ROYALE = "Battle Royale", CLASSIC = "Classic", KILL_HUNT = "Kill Hunt";
    /**
     * The name of the game mode
     */
    public final String name;
    /**
     * The description of the game mode
     */
    public final String description;

    /**
     * Constructor
     */
    public GameMode(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Get a mode by the name
     *
     * @param name of the game mode
     * @return the corresponding game mode
     */
    public static GameMode getMode(String name) {
        switch (name) {
            case CLASSIC:
                return new Classic();
            case KILL_HUNT:
                return new KillHunt();
            default:
                return new BattleRoyale();
        }
    }

    /**
     * @return the available modes
     */
    public static ArrayList<GameMode> getModes() {
        return new ArrayList<>(Arrays.asList(new BattleRoyale(), new Classic(), new KillHunt()));
    }
}
