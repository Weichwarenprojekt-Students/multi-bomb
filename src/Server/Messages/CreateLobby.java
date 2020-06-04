package Server.Messages;

public class CreateLobby extends Message {
    /**
     * Name of the lobby
     */
    public String lobbyName;

    /**
     * Constructor
     */
    public CreateLobby() {
        // Initialize the message with type
        super(Message.CREATE_LOBBY_TYPE);
    }
}
