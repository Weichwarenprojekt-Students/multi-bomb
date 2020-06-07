package Server;

import java.net.Socket;
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
    /**
     * Indicates if lobby is closed or if players can join
     */
    private boolean closed = false;

    /**
     * Constructor
     *
     * @param name name of the lobby
     */
    public Lobby(String name) {
        this.name = name;
        this.gameMode = "Battle Royale";
        this.state = WAITING;

        players = new ArrayList<>();
    }

    /**
     * Close lobby
     */
    public void close() {
        closed = true;
    }

    /**
     * Indicate if lobby is full or more players can join
     *
     * @return boolean indicating if lobby is full
     */
    public boolean isFull() {
        return players.size() >= 8;
    }

    /**
     * Indicate if lobby is open so players can join
     *
     * @return boolean indicating if lobby is open
     */
    public boolean isOpen() {
        return !closed;
    }
}
