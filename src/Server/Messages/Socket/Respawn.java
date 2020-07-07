package Server.Messages.Socket;

import Server.Messages.Message;

public class Respawn extends Message {
    /**
     * Position of the respawn point
     */
    public Position position;

    /**
     * Constructor
     *
     * @param pos position of the respawn point
     */
    public Respawn(Position pos) {
        // Initialize message with type
        super(Message.RESPAWN_TYPE);

        // set position of spawn
        this.position = pos;
    }
}
