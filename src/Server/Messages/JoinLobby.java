package Server.Messages;

public class JoinLobby extends Message {
    /**
     * Name of the lobby
     */
    public String lobbyName;
    /**
     * Name of the player who joins the lobby
     */
    public String playerID;

    /**
     * Constructor
     */
    public JoinLobby() {
        // Initialize the message with type
        super(Message.JOIN_LOBBY_TYPE);
    }
}
