package Server.Messages;

public class CloseConnection extends Message {
    /**
     * Constructor
     */
    public CloseConnection() {
        // Initialize message with type
        super(Message.CLOSE_CONNECTION_TYPE);
    }
}
