package Server;

import Game.GameModes.GameMode;
import Server.Messages.Message;
import Server.Messages.Socket.GameState;
import Server.Messages.Socket.LobbyState;
import Server.Messages.Socket.Map;
import Server.Messages.Socket.Position;

import java.util.*;

import static General.MultiBomb.LOGGER;

public class Lobby {
    /**
     * The wait time until the game starts
     */
    public static long WAIT_TIME = 5000;
    /**
     * Possible states of the lobby
     */
    public static final int WAITING = 0, GAME_STARTING = 1, IN_GAME = 2;
    /**
     * List of all players inside the lobby
     */
    public final java.util.Map<String, PlayerConnection> players;
    /**
     * Colors that are not used
     */
    private final Set<Integer> freeColors;
    /**
     * Server object of the lobby
     */
    private final Server server;
    /**
     * Random number generator for random selection of colors
     */
    private final Random random = new Random();
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
    private volatile boolean closed = false;
    /**
     * Map chosen by the host
     */
    private Map map;
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
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "Lobby(" + name + ")"));

        this.name = name;
        this.gameMode = GameMode.BATTLE_ROYALE;
        this.state = WAITING;

        this.server = server;

        players = new HashMap<>();
        freeColors = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));

        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "Lobby(" + name + ")"));
    }

    /**
     * Add player to this lobby
     *
     * @param playerConnection the player to add
     * @return boolean indicating the success of this operation
     */
    public synchronized boolean addPlayer(PlayerConnection playerConnection) {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "addPlayer(" + playerConnection.name + ")"));

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

                LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "addPlayer(" + playerConnection.name + ")"));
                return true;
            }
        }
        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "addPlayer(" + playerConnection.name + ")"));

        // players can't join
        return false;
    }

    /**
     * Remove player from this lobby
     *
     * @param playerConnection the player to remove
     */
    public synchronized void removePlayer(PlayerConnection playerConnection) {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "removePlayer(" + playerConnection.name + ")"));

        players.remove(playerConnection.name);
        setFreeColor(playerConnection.color);

        if (players.isEmpty()) {
            if (state == IN_GAME) {
                // stop game loop
                gameWorld.stopGame();
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
        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "removePlayer(" + playerConnection.name + ")"));
    }

    /**
     * Broadcast a message to all players
     *
     * @param msg the message to send
     */
    public void sendToAllPlayers(Message msg) {
        if (!msg.type.equals(Message.POSITION_TYPE)) {
            LOGGER.info(String.format("Entering: %s %s", Lobby.class.getName(), "sendToAllPlayers(" + msg.type + ")"));

            LOGGER.info(String.format("Message(%s): %s", msg.type, msg.toJson()));

            synchronized (players) {
                LOGGER.info("Message " + msg.type + " sent");
                players.values().forEach(p -> p.send(msg));
            }
            LOGGER.info(String.format("Exiting: %s %s", Lobby.class.getName(), "sendToAllPlayers(" + msg.type + ")"));
        } else {
            synchronized (players) {
                players.values().forEach(p -> p.send(msg));
            }
        }
    }

    /**
     * Get a free color and reserve it
     *
     * @return integer representing the color
     */
    public synchronized int getFreeColor() {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "getFreeColor()"));

        int i = random.nextInt(freeColors.size());

        int color = freeColors.toArray(Integer[]::new)[i];
        freeColors.remove(color);

        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "getFreeColor()"));
        return color;
    }

    /**
     * Mark color as free again
     *
     * @param color the free color
     */
    public synchronized void setFreeColor(int color) {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "setFreeColor()"));
        if (color >= 0 && color < 8) {
            freeColors.add(color);
        }
        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "setFreeColor()"));
    }

    /**
     * Get a map of players and their respective colors
     *
     * @return map of players to their colors
     */
    public synchronized HashMap<String, Integer> getPlayerColors() {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "getPlayerColors()"));

        HashMap<String, Integer> colors = new HashMap<>();
        players.forEach((k, v) -> colors.put(k, v.color));

        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "getPlayerColors()"));
        return colors;
    }

    /**
     * Update lobby with new lobby state
     *
     * @param lobbyState lobby state message
     */
    public synchronized void updateLobbyState(LobbyState lobbyState) {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "updateLobbyState()"));

        host = players.get(lobbyState.hostId);
        gameMode = lobbyState.gameMode;
        sendToAllPlayers(new LobbyState(this));

        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "updateLobbyState()"));
    }

    /**
     * Prepare game when host uploads game map
     *
     * @param map game map
     */
    public synchronized void prepareGame(Map map) {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "prepareGame()"));
        if (players.size() > 1) {
            state = GAME_STARTING;

            players.values().forEach(p -> {
                p.preparationReady = false;
                p.itemActions.clear();
                p.lastPosition = new Position(-5, -5);
            });

            sendToAllPlayers(map);
            this.map = map;
        }
        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "prepareGame()"));
    }

    /**
     * Start game if all players are ready
     */
    public synchronized void startGame() {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "startGame()"));

        // If all players are ready to start
        if (players.values().stream().allMatch(p -> p.preparationReady)) {
            state = IN_GAME;
            // Send game start message with timestamp to start countdown
            long timestamp = System.currentTimeMillis() + WAIT_TIME;
            sendToAllPlayers(GameState.running(timestamp));
            this.gameWorld = new GameWorld(this, map, timestamp);
            this.gameWorld.start();
        }

        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "startGame()"));
    }

    /**
     * End game
     *
     * @param winner winner of the game
     */
    public synchronized void endGame(String winner) {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "endGame()"));

        // Send message that game finished with name of winner to all players
        sendToAllPlayers(GameState.finished(winner));

        // set lobby into waiting state again, so next game can begin
        state = WAITING;

        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "endGame()"));
    }

    /**
     * Close lobby
     */
    public synchronized void close() {
        LOGGER.config(String.format("Entering: %s %s", Lobby.class.getName(), "close()"));

        closed = true;

        // close all remaining sockets
        players.values().forEach(p -> p.close());

        // remove lobby from the lobby list of the server
        server.removeLobby(name);

        LOGGER.config(String.format("Exiting: %s %s", Lobby.class.getName(), "close()"));
    }

    /**
     * Indicate if lobby is full or more players can join
     *
     * @return boolean indicating if lobby is full
     */
    public boolean isFull() {
        LOGGER.config(String.format("Calling: %s %s", Lobby.class.getName(), "isFull()"));
        synchronized (players) {
            return players.size() >= 8;
        }
    }

    /**
     * Indicate if lobby is open so players can join
     *
     * @return boolean indicating if lobby is open
     */
    public boolean isOpen() {
        LOGGER.config(String.format("Calling: %s %s", Lobby.class.getName(), "isOpen()"));
        return !closed;
    }
}
