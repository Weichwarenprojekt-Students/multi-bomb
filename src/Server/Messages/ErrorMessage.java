package Server.Messages;

public class ErrorMessage extends Message {
    /**
     * Error message
     */
    public String error;

    /**
     * Constructor
     */
    public ErrorMessage(String error) {
        // Initialize the message with type
        super(Message.ERROR_MESSAGE_TYPE);

        this.error = error;
    }
}
