package Server.Messages;

public class LobbyState extends Message {
    /**
     * Host of the lobby
     */
    public String hostId;
    /**
     * All players in the lobby
     */
    public String[] players;

    /**
     * Constructor
     */
    public LobbyState() {
        // Initialize message with type
        super(Message.LOBBY_STATE_TYPE);
    }
}
