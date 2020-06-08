package Server;

import Server.Messages.CloseConnection;
import Server.Messages.ErrorMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
     * Port for the server game socket
     */
    public static final int GAME_PORT = 42422;
    /**
     * Map of lobby names to their lobby objects
     */
    public final Map<String, Lobby> lobbies;
    /**
     * HTTP server thread which provides information about this server
     */
    private final HttpThread httpThread;
    /**
     * UDP Broadcast discovery thread
     */
    private final Thread discoveryThread;
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
     * Indicate if server is running
     */
    private boolean running;

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

        try {
            ServerSocket serverSocket = new ServerSocket(GAME_PORT);
            clientSocketLoop(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Listen for new socket connections and handle them
     *
     * @param serverSocket server socket which listens for new connections
     */
    public void clientSocketLoop(ServerSocket serverSocket) {
        running = true;
        while (running) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                continue;
            }

            PrintWriter out;
            try {
                // set up output stream for error output
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                try {
                    clientSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                // something with the socket went wrong, listen for the next socket connection
                continue;
            }

            String remoteIp = clientSocket.getRemoteSocketAddress().toString();

            LobbyTimestamp lobbyTimestamp;
            if (preparedPlayers.containsKey(remoteIp) && !(lobbyTimestamp = preparedPlayers.get(remoteIp)).isExpired()) {
                String lobbyName = lobbyTimestamp.lobbyName;

                Lobby lobby;
                if (lobbies.containsKey(lobbyName) && (lobby = lobbies.get(lobbyName)).isOpen()) {
                    if (!lobby.isFull()) {
                        try {
                            new PlayerConnection(clientSocket, lobby, lobbyTimestamp.playerID).start();
                            preparedPlayers.remove(remoteIp);

                            // success, listen for next socket connection
                            continue;

                        } catch (IOException e) {
                            // catch exception from PlayerConnection constructor
                            out.println(new ErrorMessage("Could not connect to lobby!"));
                        }
                    } else {
                        out.println(new ErrorMessage("Lobby is full!").toJson());
                    }
                } else {
                    out.println(new ErrorMessage("Lobby does not exist!").toJson());
                }
            } else {
                out.println(new ErrorMessage("Player could not be assigned to lobby").toJson());
            }
            preparedPlayers.remove(remoteIp);

            out.println(new CloseConnection().toJson());

            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Get all open lobbies
     *
     * @return a list of open lobbies
     */
    public synchronized List<Lobby> getLobbies() {
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
    public synchronized ErrorMessage prepareNewPlayer(String ipAddress, String lobbyName, String playerID) {
        if (lobbies.containsKey(lobbyName)) {

            Lobby lobby = lobbies.get(lobbyName);

            if (lobby.isFull()) {
                return new ErrorMessage("The requested lobby is full");
            } else if (!lobby.isOpen()) {
                lobbies.remove(lobbyName);
            } else if (lobby.getPlayerColors().containsKey(playerID)) {
                return new ErrorMessage("Name already taken, please choose a different one!");
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
    public synchronized ErrorMessage createLobby(String lobbyName) {
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
    public synchronized void closeLobby(String lobbyName) {
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
