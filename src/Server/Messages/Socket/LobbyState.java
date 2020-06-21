package Server.Messages.Socket;

import Server.Lobby;
import Server.Messages.Message;

import java.util.HashMap;

public class LobbyState extends Message {
    /**
     * Host of the lobby
     */
    public String hostId;
    /**
     * All players in the lobby and their colors
     */
    public HashMap<String, Integer> players;
    /**
     * Game Mode
     */
    public String gameMode;


    /**
     * Constructor
     */
    public LobbyState(Lobby lobby) {
        // Initialize message with type
        super(Message.LOBBY_STATE_TYPE);

        hostId = lobby.host.name;
        players = lobby.getPlayerColors();
        gameMode = lobby.gameMode;
    }

    /**
     * Constructor
     *
     * @param hostId   name of the host
     * @param gameMode name of the mode
     */
    public LobbyState(String hostId, String gameMode) {
        // Initialize message with type
        super(Message.LOBBY_STATE_TYPE);
        this.hostId = hostId;
        this.gameMode = gameMode;
    }
}
