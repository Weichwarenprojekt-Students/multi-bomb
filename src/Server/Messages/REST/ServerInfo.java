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
     * The server type (remote or local)
     */
    public String serverType;

    /**
     * Constructor
     *
     * @param server of which the information gets parsed
     */
    public ServerInfo(Server server) {
        // Initialize message with type
        super(Message.SERVER_INFO_TYPE);

        name = server.name;
        ticksPerSecond = Server.ticksPerSecond;
        lobbyCount = server.getLobbies().size();
        maxLobbies = Server.maxLobbies;
    }
}
