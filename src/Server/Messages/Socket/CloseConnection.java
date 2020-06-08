package Server.Messages.Socket;

import Server.Messages.Message;

public class CloseConnection extends Message {
    /**
     * Constructor
     */
    public CloseConnection() {
        // Initialize message with type
        super(Message.CLOSE_CONNECTION_TYPE);
    }
}
