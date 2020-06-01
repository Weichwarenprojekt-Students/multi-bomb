package Server;

import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    /**
     * Port for UPD Broadcast server discovery
     */
    public static final int UDP_PORT = 42420;
    /**
     * UDP Broadcast discovery thread
     */
    private final Thread discoveryThread;
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
     * List of all lobbies
     */
    public List<Lobby> lobbies = new ArrayList<>();

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
    }
}
