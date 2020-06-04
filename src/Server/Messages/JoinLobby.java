package Server.Messages;

public class JoinLobby extends Message {
    /**
     * Name of the lobby
     */
    public String lobbyName;

    /**
     * Constructor
     */
    public JoinLobby() {
        // Initialize the message with type
        super(Message.JOIN_LOBBY_TYPE);
    }
}
