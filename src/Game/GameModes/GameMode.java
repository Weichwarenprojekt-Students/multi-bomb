package Game.GameModes;

import Server.Messages.Socket.PlayerState;
import Server.Messages.Socket.Position;
import Server.Models.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public abstract class GameMode {
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
     * The items that are available for the game mode
     */
    public final byte[] items;
    /**
     * The player's match statistics
     */
    public HashMap<String, PlayerState> players = new HashMap<>();

    /**
     * Constructor
     */
    public GameMode(String name, String description, byte... items) {
        this.name = name;
        this.description = description;
        this.items = items;
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

    /**
     * React to game state changes (client side)
     */
    public abstract void updateClientState();

    /**
     * Calculate the winner of the game on the server
     *
     * @return Optional containing the playerId of the winner, empty Optional if there is no winner
     */
    public abstract Optional<String> calculateWinner();

    /**
     * Handle the death of a player in a special way, for example respawn
     *
     * @param player     the player that died
     * @param spawnPoint the original spawnpoint of the player
     */
    public abstract void handleDeath(Player player, Position spawnPoint);
}
