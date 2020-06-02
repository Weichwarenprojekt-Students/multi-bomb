package Server;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    public static final int WAITING = 0, GAME_STARTING = 1, IN_GAME = 2, GAME_ENDING = 3;
    /**
     * Name of the Lobby
     */
    public String name;
    /**
     * List of all players inside the lobby
     */
    public List<PlayerConnection> players;
    /**
     * Game mode
     */
    public String gameMode;
    /**
     * State of the Lobby
     */
    public int state;

    public Lobby(String name) {
        this.name = name;
        this.gameMode = "Battle Royale";
        this.state = WAITING;

        players = new ArrayList<>();
    }
}
