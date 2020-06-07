package Server;

import Server.Messages.ErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Server implements Runnable {
    /**
     * Port for UPD Broadcast server discovery
     */
    public static final int UDP_PORT = 42420;
    /**
     * Port for HTTP GET requests
     */
    public static final int HTTP_PORT = 42421;
    /**
     * HTTP server thread which provides information about this server
     */
    private final HttpThread httpThread;
    /**
     * UDP Broadcast discovery thread
     */
    private final Thread discoveryThread;
    /**
     * Map of lobby names to their lobby objects
     */
    private final Map<String, Lobby> lobbies;
    /**
     * Map of ip addresses to players that requested to join a lobby
     */
    private final Map<String, LobbyTimestamp> preparedPlayers;
    /**
     * Name of the Server
     */
    public String name;
    /**
     * Tick rate of the server
     */
    public int ticksPerSecond;
    /**
     * Maximum number of lobbies
     */
    public int maxLobbies;

    /**
     * Constructor
     *
     * @param name           name of the server
     * @param ticksPerSecond tick rate of the server
     * @param maxLobbies     maximum number of lobbies
     */
    public Server(String name, int ticksPerSecond, int maxLobbies) {
        this.name = name;
        this.ticksPerSecond = ticksPerSecond;
        this.maxLobbies = maxLobbies;

        discoveryThread = new Thread(new DiscoveryThread());
        httpThread = new HttpThread(this);

        lobbies = new HashMap<>();
        preparedPlayers = new HashMap<>();
    }

    /**
     * Constructor
     *
     * @param name of the server
     */
    public Server(String name) {
        this(name, 60, 16);
    }

    /**
     * Run server
     */
    @Override
    public void run() {
        // start UDP DiscoveryThread
        discoveryThread.start();

        // start HTTP server thread
        httpThread.start();
    }

    /**
     * Get all open lobbies
     *
     * @return a list of open lobbies
     */
    public List<Lobby> getLobbies() {
        return lobbies.values().stream().filter(l -> l.isOpen()).collect(Collectors.toList());
    }

    /**
     * Prepare a new socket connection for a player that requested to join a lobby
     *
     * @param ipAddress remote ip address of the player
     * @param lobbyName name of the lobby the player requested to join
     * @param playerID  name of the player
     * @return ErrorMessage in case of failure, null in case of success
     */
    public ErrorMessage prepareNewPlayer(String ipAddress, String lobbyName, String playerID) {
        if (lobbies.containsKey(lobbyName)) {

            Lobby lobby = lobbies.get(lobbyName);

            if (lobby.isFull()) {
                return new ErrorMessage("The requested lobby is full");
            } else if (!lobby.isOpen()) {
                lobbies.remove(lobbyName);
            } else {
                preparedPlayers.put(ipAddress, new LobbyTimestamp(lobbyName, playerID));
                return null;
            }
        }

        return new ErrorMessage("The requested lobby doesn't exist");
    }

    /**
     * Create new lobby
     *
     * @param lobbyName name of the lobby
     * @return ErrorMessage in case of failure, null in case of success
     */
    public ErrorMessage createLobby(String lobbyName) {
        if (lobbies.containsKey(lobbyName) && lobbies.get(lobbyName).isOpen()) {
            return new ErrorMessage("Lobby already exists");
        } else {
            lobbies.put(lobbyName, new Lobby(lobbyName));
            return null;
        }
    }

    /**
     * Close a lobby
     *
     * @param lobbyName name of the lobby
     */
    public void closeLobby(String lobbyName) {
        if (lobbies.containsKey(lobbyName)) {
            Lobby lobby = lobbies.get(lobbyName);
            lobbies.remove(lobbyName);
            lobby.close();
        }
    }

    private static class LobbyTimestamp {
        /**
         * Name of the lobby
         */
        public final String lobbyName;
        /**
         * Name of the player
         */
        public final String playerID;
        /**
         * Time of creation of a LobbyTimestamp object
         */
        public final long timeStamp;

        /**
         * Constructor
         *
         * @param lobbyName name of the lobby
         * @param playerID  name of the player
         */
        public LobbyTimestamp(String lobbyName, String playerID) {
            this.lobbyName = lobbyName;
            this.playerID = playerID;
            this.timeStamp = System.currentTimeMillis();
        }

        /**
         * Indicate if the timestamp is expired
         *
         * @return boolean if the timestamp is expired
         */
        public boolean isExpired() {
            return System.currentTimeMillis() - timeStamp > 10000;
        }
    }
}
