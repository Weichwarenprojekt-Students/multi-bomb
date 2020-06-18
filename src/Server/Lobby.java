package Server;

import Game.Game;
import Game.GameModes.GameMode;
import Server.Messages.Message;
import Server.Messages.Socket.GameState;
import Server.Messages.Socket.LobbyState;

import java.util.*;

public class Lobby {
    /**
     * Possible states of the lobby
     */
    public static final int WAITING = 0, GAME_STARTING = 1, IN_GAME = 2, GAME_ENDING = 3;
    /**
     * List of all players inside the lobby
     */
    public final Map<String, PlayerConnection> players;
    /**
     * Colors that are not used
     */
    private final Set<Integer> freeColors;
    /**
     * Server object of the lobby
     */
    private final Server server;
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
     * Map chosen by the host
     */
    private Game.Models.Map map;
    /**
     * GameWorld object that manages the game loop
     */
    private GameWorld gameWorld;

    /**
     * Constructor
     *
     * @param name name of the lobby
     */
    public Lobby(String name, Server server) {
        this.name = name;
        this.gameMode = GameMode.BATTLE_ROYALE;
        this.state = WAITING;

        this.server = server;

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
        if (isOpen() && state == WAITING) {
            if (players.isEmpty()) {
                // first player
                host = playerConnection;
            }

            if (!players.containsKey(playerConnection.name)) {
                this.players.put(playerConnection.name, playerConnection);
                playerConnection.color = getFreeColor();

                sendToAllPlayers(new LobbyState(this));
                return true;
            }
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
        setFreeColor(playerConnection.color);

        if (players.isEmpty()) {
            if (state == IN_GAME) {
                // stop game loop
                gameWorld.isRunning = false;
            }

            // close lobby
            close();

        } else {
            host = players.values().iterator().next();

            if (state == IN_GAME) {
                gameWorld.removePlayer(playerConnection.name);
            }

            sendToAllPlayers(new LobbyState(this));
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
    }

    /**
     * Update lobby with new lobby state
     *
     * @param lobbyState lobby state message
     */
    public synchronized void updateLobbyState(LobbyState lobbyState) {
        host = players.get(lobbyState.hostId);
        gameMode = lobbyState.gameMode;
        sendToAllPlayers(new LobbyState(this));
    }

    /**
     * Prepare game when host uploads game map
     *
     * @param map game map
     */
    public synchronized void prepareGame(Game.Models.Map map) {
        state = GAME_STARTING;
        players.values().forEach(p -> p.preparationReady = false);
        sendToAllPlayers(map);
        this.map = map;
    }

    /**
     * Start game if all players are ready
     */
    public synchronized void startGame() {
        // If all players are ready to start
        if (players.values().stream().allMatch(p -> p.preparationReady)) {
            // Send game start message with timestamp to start countdown
            long timestamp = System.currentTimeMillis();
            sendToAllPlayers(GameState.running(timestamp));
            this.gameWorld = new GameWorld(this, map, timestamp + 3000);
        }
    }

    /**
     * End game
     *
     * @param winner winner of the game
     */
    public synchronized void endGame(String winner) {
        // Send message that game finished with name of winner to all players
        sendToAllPlayers(GameState.finished(winner));

        // set lobby into waiting state again, so next game can begin
        state = WAITING;
    }

    /**
     * Close lobby
     */
    public synchronized void close() {
        closed = true;

        // close all remaining sockets
        players.values().forEach(p -> p.close());

        // remove lobby from the lobby list of the server
        server.removeLobby(name);
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
