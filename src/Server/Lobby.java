package Server;

import Server.Messages.LobbyState;
import Server.Messages.Message;

import java.util.*;

public class Lobby {
    /**
     * Possible states of the lobby
     */
    public static final int WAITING = 0, GAME_STARTING = 1, IN_GAME = 2, GAME_ENDING = 3;
    /**
     * List of all players inside the lobby
     */
    private final Map<String, PlayerConnection> players;
    /**
     * Colors that are not used
     */
    private final Set<Integer> freeColors;
    /**
     * Name of the Lobby
     */
    public String name;
    /**
     * Game mode
     */
    public String gameMode;
    /**
     * State of the Lobby
     */
    public int state;
    /**
     * Host of the lobby
     */
    public PlayerConnection host;
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

        players = new HashMap<>();
        freeColors = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
    }

    /**
     * Add player to this lobby
     *
     * @param playerConnection the player to add
     * @return boolean indicating the success of this operation
     */
    public synchronized boolean addPlayer(PlayerConnection playerConnection) {
        // this.isFull() does not need to be checked, because it can't have changed since the check in Server.java
        // this.isOpen() on the other hand might have changed because the last player has left
        if (isOpen()) {
            if (players.isEmpty()) {
                // first player
                host = playerConnection;
            }

            this.players.put(playerConnection.name, playerConnection);
            playerConnection.color = getFreeColor();

            sendToAllPlayers(new LobbyState(this));
            return true;
        }
        // players can't join
        return false;
    }

    /**
     * Remove player from this lobby
     *
     * @param playerConnection the player to remove
     */
    public synchronized void removePlayer(PlayerConnection playerConnection) {
        players.remove(playerConnection.name);

        if (players.isEmpty()) {
            closed = true;
        } else if (playerConnection == host) {
            host = players.values().iterator().next();
        }
    }

    /**
     * Broadcast a message to all players
     *
     * @param msg the message to send
     */
    public synchronized void sendToAllPlayers(Message msg) {
        players.values().forEach(p -> p.send(msg));
    }

    /**
     * Get a free color and reserve it
     *
     * @return integer representing the color
     */
    public synchronized int getFreeColor() {
        int i = freeColors.iterator().next();
        freeColors.remove(i);
        return i;
    }

    /**
     * Mark color as free again
     *
     * @param color the free color
     */
    public synchronized void setFreeColor(int color) {
        if (color >= 0 && color < 8) {
            freeColors.add(color);
        }
    }

    /**
     * Get a map of players and their respective colors
     *
     * @return map of players to their colors
     */
    public synchronized HashMap<String, Integer> getPlayerColors() {
        HashMap<String, Integer> colors = new HashMap<>();
        players.forEach((k, v) -> colors.put(k, v.color));
        return colors;
    };

    /**
     * Close lobby
     */
    public synchronized void close() {
        closed = true;
    }

    /**
     * Indicate if lobby is full or more players can join
     *
     * @return boolean indicating if lobby is full
     */
    public synchronized boolean isFull() {
        return players.size() >= 8;
    }

    /**
     * Indicate if lobby is open so players can join
     *
     * @return boolean indicating if lobby is open
     */
    public synchronized boolean isOpen() {
        return !closed;
    }
}
