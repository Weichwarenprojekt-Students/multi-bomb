package Server.Messages.Socket;

import Server.Messages.Message;

public class Respawn extends Message {

    /**
     * The id of the player
     */
    public String playerId;

    /**
     * Constructor
     */
    public Respawn(String playerId) {
        // Initialize message with type
        super(Message.RESPAWN_TYPE);
        this.playerId = playerId;
    }
}
