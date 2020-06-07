package Server.Messages;

public class CreateLobby extends Message {
    /**
     * Name of the lobby
     */
    public String lobbyName;
    /**
     * Name of the player who creates the lobby
     */
    public String playerID;

    /**
     * Constructor
     */
    public CreateLobby() {
        // Initialize the message with type
        super(Message.CREATE_LOBBY_TYPE);
    }
}
