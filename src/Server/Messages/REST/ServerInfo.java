package Server.Messages.REST;

import Server.Messages.Message;
import Server.Server;

public class ServerInfo extends Message {
    /**
     * Name of the server
     */
    public String name;
    /**
     * Tick rate of the server
     */
    public int ticksPerSecond;
    /**
     * Number of active lobbies on the server
     */
    public int lobbyCount;
    /**
     * Maximum number of lobbies
     */
    public int maxLobbies;

    /**
     * Constructor
     *
     * @param server of which the information gets parsed
     */
    public ServerInfo(Server server) {
        // Initialize message with type
        super(Message.SERVER_INFO_TYPE);

        name = server.name;
        ticksPerSecond = server.ticksPerSecond;
        lobbyCount = server.lobbies.size();
        maxLobbies = server.maxLobbies;
    }
}
